package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsAPI.IAttributeInstanceMixin;
import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AttributeInstance.class)
public class AttributeInstanceMixin implements IAttributeInstanceMixin {
    @Unique
    public AtomicDouble lr$valueFix = null;

    @WrapMethod(method = "getValue")
    private double getValue(Operation<Double> original) {
        if (lr$valueFix != null) {
            return lr$valueFix.doubleValue();
        }
        return original.call();
    }

    @Unique
    public void lr$setValueFix(AtomicDouble atomicDouble) {
        lr$valueFix = atomicDouble;
    }
}
