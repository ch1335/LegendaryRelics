package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class EntityAttributeValue implements Unit {
    private final Holder<Attribute> attributeHolder;

    public EntityAttributeValue(Holder<Attribute> attributeHolder) {
        this.attributeHolder = attributeHolder;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        LivingEntity entity = CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg);
        if (entity == null) {
            return 0;
        }
        return (float) entity.getAttributeValue(attributeHolder);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%s", Component.translatable(attributeHolder.value().getDescriptionId()).getString())).withColor(5592575);
    }

    public static EntityAttributeValue of(Holder<Attribute> attributeHolder) {
        return new EntityAttributeValue(attributeHolder);
    }
}
