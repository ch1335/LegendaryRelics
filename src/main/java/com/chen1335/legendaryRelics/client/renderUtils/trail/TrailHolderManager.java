package com.chen1335.legendaryRelics.client.renderUtils.trail;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class TrailHolderManager {
    public static final List<TrailHolder> TRAIL_HOLDERS = new ArrayList<>();

    @SubscribeEvent
    public static void RenderLevelStageEvent(RenderLevelStageEvent event) {
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            for (TrailHolder trailHolder : TRAIL_HOLDERS) {
                trailHolder.render(partialTick, event.getPoseStack(), event.getCamera());
            }
        }
    }

    @SubscribeEvent
    public static void ClientTickEvent(ClientTickEvent.Post event) {
        for (TrailHolder trailHolder : TRAIL_HOLDERS) {
            trailHolder.tick();
        }
        TRAIL_HOLDERS.removeIf(TrailHolder::needRemove);
    }

    public static void add(TrailHolder trailHolder) {
        TRAIL_HOLDERS.add(trailHolder);
    }
}
