package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.List;

/** 生物的生成放置规则与刷怪权重加成。 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LRSpawnEvents {

    @SubscribeEvent
    public static void RegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // 与原版骷髅一致：地面生成 + 通用怪物规则（够暗即可，不要求可见天空）
        event.register(LREntityTypes.GLACITE_SKELETON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    /**
     * 把稀有生物（权重占比低于阈值）的权重按范围内最高的寻觅值提高。
     *
     * <p>阈值必须用【原始】总权重算：边改边算会让占比随改动漂移，判定就没有意义了。
     * 只作用于自然刷怪，刷怪笼与结构里硬编码放置的生物不走这里。
     */
    @SubscribeEvent
    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        List<MobSpawnSettings.SpawnerData> list = event.getSpawnerDataList();
        if (list.isEmpty() || SpawnWeightBoost.threshold() <= 0.0D) {
            return;
        }

        double seeking = SpawnWeightBoost.seeking(level, event.getPos());
        if (seeking <= 0.0D) {
            return;
        }

        int total = 0;
        for (MobSpawnSettings.SpawnerData data : list) {
            total += SpawnWeightBoost.effectiveWeight(data);
        }
        if (total <= 0) {
            return;
        }

        for (MobSpawnSettings.SpawnerData data : List.copyOf(list)) {
            // 稀有判定按「权重 × 平均数量」：只刷 1 只的怪不该被当成和一次刷 4 只的一样常见
            if (!SpawnWeightBoost.isRare(SpawnWeightBoost.effectiveWeight(data), total)) {
                continue;
            }

            // 增幅仍乘在原始权重上：平均数量不变，等比放大原始权重就等于放大有效权重
            int boosted = SpawnWeightBoost.boostedWeight(data.getWeight().asInt(), seeking);
            // 必须换成带原实例的包装类，否则 canSpawnMobAt 的 contains 校验过不了
            event.removeSpawnerData(data);
            event.addSpawnerData(new SpawnWeightBoost.BoostedSpawnerData(data, boosted));
        }
    }
}
