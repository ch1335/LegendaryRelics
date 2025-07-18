package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class EquipmentEffectLevelArg implements Unit {
    private final Function<Integer, Float> floatFunction;
    private final int i;

    public EquipmentEffectLevelArg(Function<Integer, Float> floatFunction, int i) {
        this.floatFunction = floatFunction;
        this.i = i;
    }

    public static EquipmentEffectLevelArg of(Function<Integer, Float> floatFunction, int i) {
        return new EquipmentEffectLevelArg(floatFunction, i);
    }

    public static EquipmentEffectLevelArg of(Function<Integer, Float> floatFunction) {
        return of(floatFunction, 2);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        BaseEffect effect = CalculatorArg.ArgType.THIS_EQUIPMENT_EFFECT.getArgOrThrow(calculatorArg);
        int level = effect.getEffectLevel(CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg), CalculatorArg.ArgType.THIS_ITEMS_STACK.getArgOrThrow(calculatorArg));
        return floatFunction.apply(level);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%." + i + "f", getValue(calculatorArg)));
    }
}
