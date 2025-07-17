package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class EquipmentEffectArg implements Unit {
    private final Function<BaseEffect, Float> floatFunction;
    private final int i;

    public EquipmentEffectArg(Function<BaseEffect, Float> floatFunction, int i) {
        this.floatFunction = floatFunction;
        this.i = i;
    }

    public static EquipmentEffectArg of(Function<BaseEffect, Float> floatFunction, int i) {
        return new EquipmentEffectArg(floatFunction, i);
    }

    public static EquipmentEffectArg of(Function<BaseEffect, Float> floatFunction) {
        return of(floatFunction, 2);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return floatFunction.apply(CalculatorArg.ArgType.THIS_EQUIPMENT_EFFECT.getArg(calculatorArg));
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%." + i + "f", getValue(calculatorArg)));
    }
}
