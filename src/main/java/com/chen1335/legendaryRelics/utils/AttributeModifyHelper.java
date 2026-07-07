package com.chen1335.legendaryRelics.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeModifyHelper {
    public static void addAllPositive(LivingEntity livingEntity, ResourceLocation resourceLocation, float multiplier, AttributeModifier.Operation operation) {
        for (AttributeInstance value : livingEntity.getAttributes().supplier.instances.values()) {
            AttributeInstance instance = livingEntity.getAttribute(value.getAttribute());
            if (instance != null && instance.getAttribute().value().sentiment == Attribute.Sentiment.POSITIVE) {
                instance.removeModifier(resourceLocation);
                if (multiplier > 0) {
                    instance.addTransientModifier(new AttributeModifier(resourceLocation, multiplier, operation));
                }
            }
        }
    }

    public static void removeAllPositive(LivingEntity livingEntity, ResourceLocation resourceLocation) {
        for (AttributeInstance value : livingEntity.getAttributes().supplier.instances.values()) {
            AttributeInstance instance = livingEntity.getAttribute(value.getAttribute());
            if (instance != null && instance.getAttribute().value().sentiment == Attribute.Sentiment.POSITIVE) {
                instance.removeModifier(resourceLocation);
            }
        }
    }
}
