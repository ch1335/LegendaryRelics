package com.chen1335.legendaryRelics.utils;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class Utils {
    public static ResourceLocation randomLocation(int count) {
        Random random = new Random();

        StringBuilder word = new StringBuilder();
        for (int j = 0; j < count; j++) {
            char letter = (char) ('a' + random.nextInt(26));
            word.append(letter);
        }

        return LegendaryRelics.id(word.toString());
    }
}
