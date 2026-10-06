package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.LootChanceBoost;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 魔法发现：稀有条目在本抽「没被抽中」时额外独立重试一次。
 *
 * <p>重试概率由目标反解：p + (1 - p) * r = p * (1 + MF)，即 r = p * MF / (1 - p)，
 * 所以最终产出概率精确等于「基础概率 × (1 + 魔法发现)」，并封顶在 RARE_CONDITION_CHANCE_THRESHOLD。
 *
 * <p>重试只针对条目自身、不参与主池竞争，因此其它物品的概率一字不变；代价是命中时这一抽会多出一件。
 */
@Mixin(LootPool.class)
public class LootPoolMixin {
    /** 本次抽取已产出的条目，用于判断"失败"，同时保证同一条目不会被产出两次。 */
    @Unique
    private final Set<LootPoolEntry> lr$produced = Collections.newSetFromMap(new IdentityHashMap<LootPoolEntry, Boolean>());

    /** 本次抽取里各稀有条目的重试概率。 */
    @Unique
    private final Map<LootPoolEntry, Double> lr$retryChance = new IdentityHashMap<>();

    @Inject(method = "addRandomItem", at = @At("HEAD"))
    private void lr$resetSnapshot(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
        lr$produced.clear();
        lr$retryChance.clear();
    }

    /** 记录本次真正被抽中的条目（i == 1 的短路分支和扣减循环共用这个方法调用）。 */
    @Redirect(method = "addRandomItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntry;createItemStack(Ljava/util/function/Consumer;Lnet/minecraft/world/level/storage/loot/LootContext;)V"))
    private void lr$markProduced(LootPoolEntry entry, Consumer<ItemStack> consumer, LootContext context) {
        lr$produced.add(entry);
        entry.createItemStack(consumer, context);
    }

    /** 抽取结果确定前，为每个稀有条目反解出重试概率。 */
    @Inject(method = "addRandomItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private void lr$prepareRetry(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci,
                                 @Local List<LootPoolEntry> list) {
        int size = list.size();
        double factor = lr$thresholdFactor();
        double cap = LootChanceBoost.threshold();
        double magicFind = LootChanceBoost.magicFind(context);
        if (size <= 1 || factor <= 0.0D || cap <= 0.0D || magicFind <= 0.0D) {
            return;
        }

        double total = 0.0D;
        for (LootPoolEntry entry : list) {
            total += entry.getWeight(context.getLuck());
        }
        if (total <= 0.0D) {
            return;
        }

        double threshold = factor / size;
        for (LootPoolEntry entry : list) {
            double chance = entry.getWeight(context.getLuck()) / total;
            if (chance >= threshold || chance >= 1.0D) {
                continue;
            }

            // 封顶在 RARE_CONDITION_CHANCE_THRESHOLD：已达上限的条目 target - chance <= 0，重试自然为 0
            double target = Math.min(cap, chance * (1.0D + magicFind));
            double retry = (target - chance) / (1.0D - chance);
            if (retry > 0.0D) {
                lr$retryChance.put(entry, retry);
            }
        }
    }

    /** 主池结束后，对本次没产出的稀有条目各自重试一次。 */
    @Inject(method = "addRandomItem", at = @At("RETURN"))
    private void lr$retryRare(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
        if (lr$retryChance.isEmpty()) {
            return;
        }

        lr$retryChance.forEach((entry, chance) -> {
            if (!lr$produced.contains(entry) && context.getRandom().nextDouble() < chance) {
                entry.createItemStack(consumer, context);
            }
        });
    }

    /** 服务端配置在进入世界前没有加载，此时 get() 会抛异常，退回默认值。 */
    @Unique
    private static double lr$thresholdFactor() {
        return LRServerConfig.CONFIG_SPEC.isLoaded()
                ? LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.get()
                : LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.getDefault();
    }
}
