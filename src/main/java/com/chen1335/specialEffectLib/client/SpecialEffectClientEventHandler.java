package com.chen1335.specialEffectLib.client;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class SpecialEffectClientEventHandler {

    @EventBusSubscriber(value = Dist.CLIENT, modid = LegendaryRelics.MODID)
    public static class Mod {
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(
                    ResourceLocation.fromNamespaceAndPath(SpecialEffectLib.MODID, "special_effect_icon"),
                    new SpecialEffectIconRender()
            );
        }
    }
}
