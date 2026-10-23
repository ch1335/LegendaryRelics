package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public class EquipmentEffectLevelArg extends FormatedOperation {


    public static final MapCodec<EquipmentEffectLevelArg> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("value").forGetter(arg -> arg.levelBasedValue),
            Format.CODEC.optionalFieldOf("format", Format.NORMAL).forGetter(arg -> arg.format)
    ).apply(instance, EquipmentEffectLevelArg::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentEffectLevelArg> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TAG,
            equipmentEffectLevelArg -> LevelBasedValue.CODEC.encodeStart(NbtOps.INSTANCE, equipmentEffectLevelArg.levelBasedValue).getOrThrow(),
            Format.STREAM_CODEC,
            equipmentEffectLevelArg -> equipmentEffectLevelArg.format,
            (tag, format) -> new EquipmentEffectLevelArg(LevelBasedValue.CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst(), format)
    );

    private final LevelBasedValue levelBasedValue;

    public EquipmentEffectLevelArg(LevelBasedValue floatFunction) {
        this(floatFunction, Format.NORMAL);
    }

    public EquipmentEffectLevelArg(LevelBasedValue floatFunction, Format format) {
        this.levelBasedValue = floatFunction;
        this.format = format;
    }

    public static EquipmentEffectLevelArg of(LevelBasedValue floatFunction) {
        return new EquipmentEffectLevelArg(floatFunction);
    }

    public static EquipmentEffectLevelArg of(LevelBasedValue floatFunction, Format format) {
        return new EquipmentEffectLevelArg(floatFunction, format);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        BaseEffect effect = CalculatorArg.ArgType.THIS_EQUIPMENT_EFFECT.getArgOrThrow(calculatorArg);
        int level = effect.getEffectLevel(CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg), CalculatorArg.ArgType.THIS_ITEMS_STACK.getArgOrThrow(calculatorArg));
        return levelBasedValue.calculate(level);
    }

    @Override
    public Component expand(CalculatorArg calculatorArg) {
        return formatValue(getValue(calculatorArg));
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
        return CalculatorRegister.EQUIPMENT_EFFECT_LEVEL.get();
    }
}
