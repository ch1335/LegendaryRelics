package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.curios.api.SlotContext;

public class AttributeModifierHolder {
    private final float amount;
    private final AttributeModifier.Operation operation;

    public AttributeModifierHolder(float amount, AttributeModifier.Operation operation) {
        this.amount = amount;
        this.operation = operation;
    }

    public float getAmount() {
        return amount;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public AttributeModifier toAttributeModifier(SlotContext context) {
        return new AttributeModifier(LegendaryRelics.id("modifier_" + context.identifier() + "_" + context.index()), amount, operation);
    }
}
