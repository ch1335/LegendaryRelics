package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LRAttributes;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/**
 * 魔法发现对「概率型战利品」的统一处理。
 *
 * <p>只提升低于阈值的基础概率，且结果是「基础 × (1 + 魔法发现)」，不影响任何其它掉落。
 */
public final class LootChanceBoost {
    private LootChanceBoost() {
    }

    /**
     * 服务端配置在进入世界前没有加载，此时 get() 会抛异常，退回默认值。
     */
    public static double threshold() {
        return LRServerConfig.CONFIG_SPEC.isLoaded()
                ? LRServerConfig.RARE_CONDITION_CHANCE_THRESHOLD.get()
                : LRServerConfig.RARE_CONDITION_CHANCE_THRESHOLD.getDefault();
    }

    /**
     * 战利品上下文里取玩家：箱子/方块是 THIS_ENTITY，怪物掉落是 LAST_DAMAGE_PLAYER，钓鱼是 ATTACKING_ENTITY。
     */
    public static double magicFind(LootContext context) {
        Entity source = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        if (source == null) {
            source = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        }
        if (source == null) {
            source = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        }
        return source instanceof LivingEntity living ? (living.getAttributes().hasAttribute(LRAttributes.MAGIC_FIND) ? living.getAttributeValue(LRAttributes.MAGIC_FIND) : 0) : 0.0D;
    }

    /**
     * 稀有概率按「基础 × (1 + 魔法发现)」提升，且封顶在阈值本身；非稀有、或没有属性时原样返回。
     */
    public static float boost(float chance, LootContext context) {
        double threshold = threshold();
        if (chance <= 0.0F || threshold <= 0.0D || chance >= threshold) {
            return chance;
        }

        double magicFind = magicFind(context);
        if (magicFind <= 0.0D) {
            return chance;
        }

        return (float) Math.min(threshold, chance * (1.0D + magicFind));
    }
}
