package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.API.objects.LRBlockEntities;
import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRMenus;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.gui.EffectCooldownRender;
import com.chen1335.legendaryRelics.client.renderers.blockEntityRenderers.IceEncasementRenderer;
import com.chen1335.legendaryRelics.client.renderers.curioRenderers.HolyCloakRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.FlyingKnifeRender;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.FlyingReaperRender;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.FrostswornRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.GlaciteSkeletonRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.IcicleRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.OrbitingFireChakramRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.TreatmentBallRenderer;
import com.chen1335.legendaryRelics.client.renderers.entityRenderers.ThrownFrostShardRender;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.ISSModProxy;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.client.SoulMissileRenderer;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.screens.EquipmentWorkbenchCraftScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
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

        @SubscribeEvent
        public static void EntityTickEvent(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity living) {
                LREntityData.getData(living).cloakRender.tick(living);
            }
        }

        @SubscribeEvent
        public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {

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
            event.registerEntityRenderer(LREntityTypes.THROWN_FROST_SHARD.value(), ThrownFrostShardRender::new);
            event.registerEntityRenderer(LREntityTypes.ICICLE.value(), IcicleRenderer::new);
            event.registerEntityRenderer(LREntityTypes.ORBITING_FIRE_CHAKRAM.value(), OrbitingFireChakramRenderer::new);
            event.registerEntityRenderer(LREntityTypes.GLACITE_SKELETON.value(), GlaciteSkeletonRenderer::new);
            event.registerEntityRenderer(LREntityTypes.FROSTSWORN.value(), FrostswornRenderer::new);
            event.registerBlockEntityRenderer(LRBlockEntities.ICE_ENCASEMENT.get(), IceEncasementRenderer::new);
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

        /**
         * 冰封方块用原版冰的贴图，而冰是半透明的。
         *
         * <p>方块渲染层由 {@code ItemBlockRenderTypes} 决定，自定义方块默认走实体（不透明）层，
         * 那样冰会变成实心白块、也看不到里面的物品，所以必须显式注册成半透明层。
         */
        @SubscribeEvent
        public static void RegisterBlockRenderLayers(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(LRBlocks.ICE_ENCASEMENT.get(), RenderType.translucent()));
        }

        /** 动画里用的 query.modified_move_speed 不是 GeckoLib 内置查询，不注册的话恒为 0。 */
        @SubscribeEvent
        public static void RegisterMolangQueries(FMLClientSetupEvent event) {
            LRMolangQueries.register();
        }
    }
}
