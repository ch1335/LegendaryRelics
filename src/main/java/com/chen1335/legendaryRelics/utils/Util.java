package com.chen1335.legendaryRelics.utils;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class Util {
    @Calculator
    public static FinalCalculator ARROW_DAMAGE_MUL = FinalCalculator.of(
            Mul.of(
                    EntityAttributeValue.of(AttributesGetter.arrowDamage()),
                    EntityAttributeValue.of(AttributesGetter.arrowVelocity())
            )
    );

    //Random ResourceLocation
    public static ResourceLocation randomLocation(int count) {
        return randomLocation(LegendaryRelics.MODID, count);
    }

    public static ResourceLocation randomLocation(String nameSpace, int count) {
        Random random = new Random();

        StringBuilder word = new StringBuilder();
        for (int j = 0; j < count; j++) {
            char letter = (char) ('a' + random.nextInt(26));
            word.append(letter);
        }

        return ResourceLocation.fromNamespaceAndPath(nameSpace, word.toString());
    }
}
