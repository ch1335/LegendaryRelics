package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class AutoRegister {
    public static void init() {
        List<ModFileScanData> data = ModList.get().getAllScanData();

        for (ModFileScanData datum : data) {
            datum.getAnnotatedBy(Calculator.class, ElementType.FIELD).forEach(annotationData -> {
                try {
                    Class<?> clazz = AutoRegister.class.getClassLoader().loadClass(annotationData.clazz().getClassName());
                    String fieldName = annotationData.memberName();
                    Field field = clazz.getDeclaredField(fieldName);
                    if (Modifier.isStatic(field.getModifiers())) {
                        CalculatorsHolder.register(new CalculatorsHolder.LocateInfo(clazz, fieldName), (FinalCalculator) field.get(null));
                    }
                } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }
}
