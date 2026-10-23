package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

/** 生物的生成放置规则。 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LRSpawnEvents {

    @SubscribeEvent
    public static void RegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // 与原版骷髅一致：地面生成 + 通用怪物规则（够暗即可，不要求可见天空）
        event.register(LREntityTypes.GLACIATE_SKELETON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
