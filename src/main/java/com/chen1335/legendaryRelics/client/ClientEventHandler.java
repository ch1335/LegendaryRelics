package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.entityRenderers.FlyingReaperRender;
import com.chen1335.legendaryRelics.client.entityRenderers.TreatmentBallRenderer;
import com.chen1335.legendaryRelics.client.gui.EffectCooldownRender;
import com.chen1335.legendaryRelics.config.LootConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ClientEventHandler {
    @EventBusSubscriber(value = {Dist.CLIENT})
    public static class Game {
        @SubscribeEvent
        public static void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (Minecraft.getInstance().getSingleplayerServer() != null) {
                LootConfig.load();
            }
        }

        @SubscribeEvent
        public static void ClientTickEvent(ClientTickEvent.Pre event) {
            Player clientPlayer = LRClient.getClientPlayer();
            if (clientPlayer != null) {
                if (!Minecraft.getInstance().isPaused()) {
                    LRClient.getClientCooldownManager().tick(clientPlayer);
                }
            }
        }
    }

    @EventBusSubscriber(value = {Dist.CLIENT})
    public static class Mod {
        @SubscribeEvent
        public static void RegisterGuiLayersEvent(RegisterGuiLayersEvent event) {
            event.registerAboveAll(LegendaryRelics.id("effect_cooldown"), new EffectCooldownRender());
        }

        @SubscribeEvent
        public static void RegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(LREntityTypes.TREATMENT_BALL.value(), TreatmentBallRenderer::new);
            event.registerEntityRenderer(LREntityTypes.FLYING_REAPER.value(), FlyingReaperRender::new);
        }
    }
}
