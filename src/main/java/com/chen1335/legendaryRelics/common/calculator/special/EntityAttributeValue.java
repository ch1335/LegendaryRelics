package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class EntityAttributeValue implements OperationUnit {
    public static final MapCodec<EntityAttributeValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(value -> value.attributeHolder)
    ).apply(instance, EntityAttributeValue::new));

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
    public Component expand(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%s", Component.translatable(attributeHolder.value().getDescriptionId()).getString())).withColor(5592575);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<? extends OperationUnit> getCodec() {
        return CODEC;
    }

    @Override
    public OperationType getType() {
        return CalculatorRegister.ENTITY_ATTRIBUTE.get();
    }

    public static EntityAttributeValue of(Holder<Attribute> attributeHolder) {
        return new EntityAttributeValue(attributeHolder);
    }
}
