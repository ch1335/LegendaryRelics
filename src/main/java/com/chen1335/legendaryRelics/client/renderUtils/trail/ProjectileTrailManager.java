package com.chen1335.legendaryRelics.client.renderUtils.trail;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class ProjectileTrailManager {
    public static final List<ProjectileTrail> PROJECTILE_TRAILS = new ArrayList<>();

    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event) {
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            for (ProjectileTrail trail : PROJECTILE_TRAILS) {
                trail.render(partialTick, event.getPoseStack(), event.getCamera());
            }
        }
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post event) {
        for (ProjectileTrail trail : PROJECTILE_TRAILS) {
            trail.tick();
        }
        PROJECTILE_TRAILS.removeIf(ProjectileTrail::needRemove);
    }

    public static void add(ProjectileTrail trail) {
        PROJECTILE_TRAILS.add(trail);
    }

    public static void clear() {
        PROJECTILE_TRAILS.clear();
    }
}
