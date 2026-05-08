package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public class EquipmentEffectLevelArg implements Unit {
    private final LevelBasedValue levelBasedValue;
    private final int i;


    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentEffectLevelArg> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.TAG,
            equipmentEffectLevelArg -> LevelBasedValue.CODEC.encodeStart(NbtOps.INSTANCE, equipmentEffectLevelArg.levelBasedValue).getOrThrow(),
            (c) -> EquipmentEffectLevelArg.of(LevelBasedValue.CODEC.decode(NbtOps.INSTANCE, c).getOrThrow().getFirst())
    );

    public EquipmentEffectLevelArg(LevelBasedValue floatFunction, int i) {
        this.levelBasedValue = floatFunction;
        this.i = i;


    }

    public static EquipmentEffectLevelArg of(LevelBasedValue floatFunction, int i) {
        return new EquipmentEffectLevelArg(floatFunction, i);
    }

    public static EquipmentEffectLevelArg of(LevelBasedValue floatFunction) {
        return of(floatFunction, 2);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        BaseEffect effect = CalculatorArg.ArgType.THIS_EQUIPMENT_EFFECT.getArgOrThrow(calculatorArg);
        int level = effect.getEffectLevel(CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg), CalculatorArg.ArgType.THIS_ITEMS_STACK.getArgOrThrow(calculatorArg));
        return levelBasedValue.calculate(level);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(FinalCalculator.format(getValue(calculatorArg), i));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }
}
