package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.BlackDragon;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static BlackDragon<LivingEntity> TEST_RENDER;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlackDragon.LAYER_LOCATION, BlackDragon::createBodyLayer);
    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        TEST_RENDER = new BlackDragon<>(event.getContext().bakeLayer(BlackDragon.LAYER_LOCATION));
    }
}
