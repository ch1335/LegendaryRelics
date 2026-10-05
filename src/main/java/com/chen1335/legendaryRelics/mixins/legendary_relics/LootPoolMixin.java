package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.API.objects.LRAttributes;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 稀有掉落补偿：概率低于「均分概率 × 阈值系数」的条目，按玩家的魔法发现属性临时提高权重。
 *
 * <p>原版扣减循环要求 Σ getWeight() 等于传给 nextInt 的分母，所以这里直接返回 total + 额外权重，
 * 否则靠后的条目会抽不到，甚至 nextInt(0) 抛异常。
 *
 * <p>总权重从 nextInt 的参数拿，而不是捕获 MutableInt 局部变量：后者不在 LVT 里（被 lambda 捕获后收缩），
 * 而 @Local 按 LVT 匹配，注定匹配不到。
 */
@Mixin(LootPool.class)
public class LootPoolMixin {
    @Unique
    private final Map<LootPoolEntry, Integer> lr$rareBonus = new IdentityHashMap<>();

    @Inject(method = "addRandomItem", at = @At("HEAD"))
    private void lr$resetSnapshot(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
        lr$rareBonus.clear();
    }

    /** 判定稀有、随机取整，并把额外权重加回分母，与抽取时加的权重保持一致。 */
    @ModifyArg(method = "addRandomItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int lr$applyRareBonus(int total,
                                  @Local List<LootPoolEntry> list,
                                  @Local(argsOnly = true) LootContext context) {
        int size = list.size();
        double thresholdFactor = lr$thresholdFactor();
        double rate = 1.0D + lr$magicFind(context);
        if (size <= 1 || total <= 0 || thresholdFactor <= 0.0D || rate <= 1.0D) {
            return total;
        }

        double threshold = thresholdFactor / size;
        int bonusSum = 0;
        for (LootPoolEntry entry : list) {
            int weight = entry.getWeight(context.getLuck());
            if (weight / (double) total >= threshold) {
                continue;
            }

            int bonus = lr$stochasticRound(weight * rate, context) - weight;
            if (bonus > 0) {
                lr$rareBonus.put(entry, bonus);
                bonusSum += bonus;
            }
        }

        return total + bonusSum;
    }

    /** 抽取时为稀有掉落补上额外权重，与分母上加进去的总和保持一致。 */
    @Redirect(method = "addRandomItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/entries/LootPoolEntry;getWeight(F)I"))
    private int lr$addRareBonus(LootPoolEntry entry, float luck) {
        return entry.getWeight(luck) + lr$rareBonus.getOrDefault(entry, 0);
    }

    /** 服务端配置在进入世界前没有加载，此时 get() 会抛异常，退回默认值。 */
    @Unique
    private static double lr$thresholdFactor() {
        return LRServerConfig.CONFIG_SPEC.isLoaded()
                ? LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.get()
                : LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.getDefault();
    }

    /** 战利品上下文里取玩家：箱子是 THIS_ENTITY，怪物掉落是 LAST_DAMAGE_PLAYER，钓鱼是 ATTACKING_ENTITY。 */
    @Unique
    private static double lr$magicFind(LootContext context) {
        Entity source = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        if (source == null) {
            source = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        }
        if (source == null) {
            source = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        }
        return source instanceof LivingEntity living ? living.getAttributeValue(LRAttributes.MAGIC_FIND) : 0.0D;
    }

    /** 随机取整：整数部分保留，小数部分按比例进位，期望等于原值。 */
    @Unique
    private int lr$stochasticRound(double value, LootContext context) {
        int floor = Mth.floor(value);
        double fraction = value - floor;
        if (fraction > 0.0D && context.getRandom().nextDouble() < fraction) {
            return floor + 1;
        }
        return floor;
    }
}
