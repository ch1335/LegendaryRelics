package com.chen1335.specialEffectLib.common;


import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class EventHandler {
    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Game {
        @SubscribeEvent
        public static void EntityTickEvent(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).tick(livingEntity);
            }
        }
    }

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Mod {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(RegisterTypes.SPECIAL_EFFECT_TYPE);
        }
    }
}
