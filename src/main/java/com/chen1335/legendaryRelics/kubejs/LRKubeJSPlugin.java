package com.chen1335.legendaryRelics.kubejs;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorsHolder;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.normal.*;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.common.calculator.special.SingleCustomArg;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

import java.util.HashSet;
import java.util.Set;

public class LRKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("CalculatorsHolder", CalculatorsHolder.class);

        bindings.add("CalculatorAdd", Add.class);
        bindings.add("CalculatorMultiAdd", MultiAdd.class);
        bindings.add("CalculatorArg", CalculatorArg.class);
        bindings.add("CalculatorArgType", CalculatorArg.ArgType.class);
        bindings.add("CalculatorConstant", Constant.class);
        bindings.add("CalculatorDarkGoldUpdateArg", DarkGoldUpdateArg.class);
        bindings.add("CalculatorEntityAttributeValue", EntityAttributeValue.class);
        bindings.add("CalculatorEquipmentEffectLevelArg", EquipmentEffectLevelArg.class);
        bindings.add("CalculatorFinal", FinalCalculator.class);
        bindings.add("CalculatorMul", Mul.class);
        bindings.add("CalculatorMultiMul", MultiMul.class);
        bindings.add("CalculatorSingleCustomArg", SingleCustomArg.class);
        Set<Class<?>> classes = new HashSet<>();
        for (CalculatorsHolder.LocateInfo locateInfo : CalculatorsHolder.getCalculators().keySet()) {
            try {
                if (!locateInfo.className().equals("kubejs")) {
                    classes.add(Class.forName(locateInfo.className()));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for (Class<?> aClass : classes) {
            bindings.add(aClass.getSimpleName(), aClass);
        }

    }
}
