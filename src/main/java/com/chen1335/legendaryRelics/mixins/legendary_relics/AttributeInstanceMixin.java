package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.attributeFix.fixTypes.BaseFix;
import com.chen1335.legendaryRelics.mixinsAPI.IAttributeInstanceExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements IAttributeInstanceExtension {
    @Shadow private boolean dirty;

    @Shadow protected abstract double calculateValue();

    @Shadow private double cachedValue;
    @Unique
    public BaseFix lr$valueFix = null;

    @WrapMethod(method = "getValue")
    private double getValue(Operation<Double> original) {
        if (lr$valueFix != null) {
            return lr$valueFix.getValue(original.call());
        }
        return original.call();
    }

    @Unique
    public void lr$setValueFix(BaseFix fix) {
        lr$valueFix = fix;
    }

    @Override
    public double lr$getTrueValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }

        return this.cachedValue;
    }


}
