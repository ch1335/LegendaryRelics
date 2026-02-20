package com.chen1335.legendaryRelics.common.attributeFix;

import com.chen1335.legendaryRelics.common.attributeFix.fixTypes.BaseFix;
import com.chen1335.legendaryRelics.mixinsAPI.IAttributeInstanceExtension;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class AttributeFixer {
    public static void runWhileFix(LivingEntity livingEntity, Holder<Attribute> attribute, BaseFix fix, Runnable runnable) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance == null) {
            return;
        }
        ((IAttributeInstanceExtension) attributeInstance).lr$setValueFix(fix);

        try {
            runnable.run();
            ((IAttributeInstanceExtension) attributeInstance).lr$setValueFix(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ((IAttributeInstanceExtension) attributeInstance).lr$setValueFix(null);
        }
    }


}
