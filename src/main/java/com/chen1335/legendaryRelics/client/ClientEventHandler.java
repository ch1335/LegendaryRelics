package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.entityRenderers.TreatmentBallRenderer;
import com.chen1335.legendaryRelics.client.gui.EffectCooldownRender;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ClientEventHandler {
    @EventBusSubscriber(value = {Dist.CLIENT}, bus = EventBusSubscriber.Bus.GAME)
    public static class Game {
        @SubscribeEvent
        public static void ClientTickEvent(ClientTickEvent.Pre event) {
            Player clientPlayer = LRClient.getClientPlayer();
            if (clientPlayer != null) {
                LRClient.getClientCooldownManager().tick(clientPlayer);
            }
        }
    }

    @EventBusSubscriber(value = {Dist.CLIENT}, bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void RegisterGuiLayersEvent(RegisterGuiLayersEvent event) {
            event.registerAboveAll(LegendaryRelics.id("effect_cooldown"), new EffectCooldownRender());
        }

        @SubscribeEvent
        public static void RegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(LREntityTypes.TREATMENT_BALL.value(), TreatmentBallRenderer::new);
        }
    }
}
