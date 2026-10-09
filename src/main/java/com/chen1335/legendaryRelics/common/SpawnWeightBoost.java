package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LRAttributes;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * 寻觅对「稀有刷怪权重」的统一处理。
 *
 * <p>只提升权重占比低于阈值的生物，结果为「基础权重 × (1 + 寻觅)」且不封顶，不影响其它生物。
 */
public final class SpawnWeightBoost {
    /** 与 {@code NaturalSpawner.SPAWN_DISTANCE_BLOCK} 一致：刷怪只发生在玩家 128 格内。 */
    public static final double SEEKING_RADIUS = 128.0D;

    private SpawnWeightBoost() {
    }

    /** 服务端配置在进入世界前没有加载，此时 get() 会抛异常，退回默认值。 */
    public static double threshold() {
        return LRServerConfig.CONFIG_SPEC.isLoaded()
                ? LRServerConfig.RARE_SPAWN_WEIGHT_THRESHOLD.get()
                : LRServerConfig.RARE_SPAWN_WEIGHT_THRESHOLD.getDefault();
    }

    /**
     * 取范围内所有玩家的寻觅值里最高的那个。
     *
     * <p>刷怪没有「贡献者」概念（伤害来源、离得最近都不代表什么），所以只能按范围内的最强值算。
     */
    public static double seeking(ServerLevel level, BlockPos pos) {
        double radiusSqr = SEEKING_RADIUS * SEEKING_RADIUS;
        double best = 0.0D;

        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > radiusSqr) {
                continue;
            }

            best = Math.max(best, player.getAttributeValue(LRAttributes.SEEKING));
        }

        return best;
    }

    /**
     * 「有效权重」= 权重 × 平均每次刷新的数量，向上取整。
     *
     * <p>一次刷 4 只的怪，实际出现量本来就是刷 1 只的 4 倍，只看权重会低估它。
     * 用整数算（sum = min + max，所以乘的是两倍平均），避免浮点误差让向上取整多进一位。
     */
    public static int effectiveWeight(MobSpawnSettings.SpawnerData data) {
        int weight = Math.max(data.getWeight().asInt(), 0);
        int sum = data.minCount + data.maxCount;

        return (weight * sum + 1) / 2;
    }

    /** 该生物的有效权重在整张表里的占比是否低于阈值。 */
    public static boolean isRare(int weight, int total) {
        double threshold = threshold();

        return threshold > 0.0D && total > 0 && (double) Math.max(weight, 0) / total < threshold;
    }

    /** 新权重 = 原权重 × (1 + 寻觅)。至少留 1，否则会被 WeightedRandomList 直接丢掉。 */
    public static int boostedWeight(int weight, double seeking) {
        return Math.max(1, (int) Math.round(weight * (1.0D + seeking)));
    }

    /**
     * 带增幅权重的刷怪项，内部存着被替换掉的原实例。
     *
     * <p>不这么做就刷不出来：{@code NaturalSpawner.canSpawnMobAt} 会重算一次刷怪表，再拿「被抽中的那个实例」
     * 去 {@code List.contains} 比对。而 {@code SpawnerData} 没有重写 equals（退化成引用比较），
     * 换了实例就必然比对失败，于是稀有生物全被拒。这里让 equals 降级成比原实例，就能重新通过。
     *
     * <p>前提是两次事件里拿到的原实例是同一个引用 —— 刷怪表由群系对象烘焙后持有，正常成立。
     */
    public static class BoostedSpawnerData extends MobSpawnSettings.SpawnerData {
        private final MobSpawnSettings.SpawnerData original;

        public BoostedSpawnerData(MobSpawnSettings.SpawnerData original, int weight) {
            super(original.type, weight, original.minCount, original.maxCount);
            this.original = original;
        }

        @Override
        public boolean equals(Object other) {
            MobSpawnSettings.SpawnerData target = other instanceof BoostedSpawnerData boosted
                    ? boosted.original
                    : other instanceof MobSpawnSettings.SpawnerData data ? data : null;

            return this.original == target;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.original);
        }
    }
}
