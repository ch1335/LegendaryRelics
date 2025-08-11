package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.mixinsAPI.IAttributeInstanceMixin;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class AttributeFixer {
    public static void runWhileFix(LivingEntity livingEntity, Holder<Attribute> attribute, double targetValue, Runnable runnable) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            ((IAttributeInstanceMixin) attributeInstance).lr$setValueFix(new AtomicDouble(targetValue));
        }

        try {
            runnable.run();
            if (attributeInstance != null) {
                ((IAttributeInstanceMixin) attributeInstance).lr$setValueFix(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (attributeInstance != null) {
                ((IAttributeInstanceMixin) attributeInstance).lr$setValueFix(null);
            }
        }


    }
}
