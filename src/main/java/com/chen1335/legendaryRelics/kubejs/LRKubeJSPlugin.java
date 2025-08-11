package com.chen1335.legendaryRelics.kubejs;

import com.chen1335.legendaryRelics.common.calculator.*;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class LRKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry bindings) {
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
    }
}
