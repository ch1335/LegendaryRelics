package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class EntityAttributeValue implements Unit {
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributeValue> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE),
            entityAttributeValue -> entityAttributeValue.attributeHolder,
            EntityAttributeValue::new
    );

    private final Holder<Attribute> attributeHolder;

    private EntityAttributeValue(Holder<Attribute> attributeHolder) {
        this.attributeHolder = attributeHolder;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        LivingEntity entity = CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg);

        if (entity == null) {
            return (float) attributeHolder.value().getDefaultValue();
        }
        return (float) entity.getAttributeValue(attributeHolder);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%s", Component.translatable(attributeHolder.value().getDescriptionId()).getString())).withColor(5592575);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }

    public static EntityAttributeValue of(Holder<Attribute> attributeHolder) {
        return new EntityAttributeValue(attributeHolder);
    }
}
