package com.chen1335.specialEffectLib.common;


import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.chen1335.specialEffectLib.network.AddOrUpdateEffectPack;
import com.chen1335.specialEffectLib.network.SyncAllEffectPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.Map;

public class EventHandler {
    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Game {
        @SubscribeEvent
        public static void EntityTickEvent(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).tick(livingEntity);
            }
        }

        @SubscribeEvent
        public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                for (Map<MobEffectType<?>, SpecialMobEffect> value : livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).getEffects().values()) {
                    for (SpecialMobEffect effect : value.values()) {
                        effect.onAddOrUpdate(livingEntity);
                    }
                }
            }
        }


        @SubscribeEvent
        public static void EntityJoinLevelEvent(EntityLeaveLevelEvent event) {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                for (Map<MobEffectType<?>, SpecialMobEffect> value : livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).getEffects().values()) {
                    for (SpecialMobEffect effect : value.values()) {
                        effect.onRemove(livingEntity);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void StartTracking(PlayerEvent.StartTracking event) {
            Player player = event.getEntity();
            if (!player.level().isClientSide && event.getTarget() instanceof LivingEntity livingEntity && livingEntity.hasData(SEAttachmentTypes.ENTITY_EFFECT_DATA)) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncAllEffectPack(livingEntity.getId(), livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA)));
            }
        }

        @SubscribeEvent
        public static void PlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            if (!player.level().isClientSide && player.hasData(SEAttachmentTypes.ENTITY_EFFECT_DATA)) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncAllEffectPack(player.getId(), player.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA)));
            }
        }

    }

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Mod {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(RegisterTypes.SPECIAL_EFFECT_TYPE);
        }

        @SubscribeEvent
        public static void RegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(SyncAllEffectPack.TYPE, SyncAllEffectPack.STREAM_CODEC, SyncAllEffectPack::handler);
            registrar.playToClient(AddOrUpdateEffectPack.TYPE, AddOrUpdateEffectPack.STREAM_CODEC, AddOrUpdateEffectPack::handler);
        }
    }
}
