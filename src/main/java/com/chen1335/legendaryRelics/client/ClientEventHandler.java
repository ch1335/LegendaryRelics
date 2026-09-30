package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRMenus;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.FlyingKnifeRender;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.FlyingReaperRender;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.TreatmentBallRenderer;
import com.chen1335.legendaryRelics.client.gui.EffectCooldownRender;
import com.chen1335.legendaryRelics.client.renderers.curioRenderers.HolyCloakRenderer;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.ISSModProxy;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.client.SoulMissileRenderer;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.screens.EquipmentWorkbenchCraftScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class ClientEventHandler {
    @EventBusSubscriber(value = {Dist.CLIENT})
    public static class Game {
        @SubscribeEvent
        public static void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (Minecraft.getInstance().getSingleplayerServer() != null) {

            }
        }

        @SubscribeEvent
        public static void ClientTickEvent(ClientTickEvent.Pre event) {
            Player clientPlayer = LRClient.getClientPlayer();
            if (clientPlayer != null) {
                if (!Minecraft.getInstance().isPaused()) {
                    LRClient.getClientCooldownManager().tick(clientPlayer);
                }

                if (clientPlayer.getItemBySlot(EquipmentSlot.CHEST).getItem() == LRItems.BLACK_DRAGON_CHEST_PLATE.get() &&
                        clientPlayer.isFallFlying() && Minecraft.getInstance().options.keyJump.isDown()) {
                    Vec3 vec31 = clientPlayer.getLookAngle();
                    Vec3 vec32 = clientPlayer.getDeltaMovement();
                    float mul = 0.1F;
                    if (Minecraft.getInstance().options.keyShift.isDown()) {
                        mul = 0.5F;
                    }
                    clientPlayer.setDeltaMovement(
                            vec32.add(
                                    vec31.x * mul + (vec31.x * 1.5 - vec32.x) * 0.5,
                                    vec31.y * mul + (vec31.y * 1.5 - vec32.y) * 0.5,
                                    vec31.z * mul + (vec31.z * 1.5 - vec32.z) * 0.5
                            )
                    );
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
            event.registerEntityRenderer(LREntityTypes.FLYING_KNIFE.value(), FlyingKnifeRender::new);
            if (ISSModProxy.INSTANCE.shouldLoad()) {
                event.registerEntityRenderer(ISSRegisters.Entity.SOUL_MISSILE.value(), SoulMissileRenderer::new);
            }
        }

        @SubscribeEvent
        public static void RegisterMenuScreensEvent(RegisterMenuScreensEvent event) {
            event.register(LRMenus.EQUIPMENT_WORKBENCH_CRAFT.value(), EquipmentWorkbenchCraftScreen::new);
        }

        /**
         * 注册饰品渲染器。
         *
         * <p>Curios 会在它自己的 {@code EntityRenderersEvent.AddLayers} 监听器里调用
         * {@code CuriosRendererRegistry.load()}，而 {@code getRenderer} 只读 load 时填充的表。
         * 所以这里必须用最高优先级，确保在它之前完成注册。
         */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void RegisterCurioRenderers(EntityRenderersEvent.AddLayers event) {
            CuriosRendererRegistry.register(LRItems.HOLY_CLOAK.get(), HolyCloakRenderer::new);
        }
    }
}
