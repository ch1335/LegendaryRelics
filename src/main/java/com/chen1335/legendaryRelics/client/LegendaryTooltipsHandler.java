package com.chen1335.legendaryRelics.client;

import com.anthonyhilyard.iceberg.util.Selectors;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.world.item.Rarity;
import com.chen1335.equipmentEffectLib.utils.Cast;

import java.lang.reflect.Field;
import java.util.Map;

public class LegendaryTooltipsHandler {
    public static void init() {
        try {
            Field declaredField = Selectors.class.getDeclaredField("rarities");
            declaredField.setAccessible(true);
            Map<String, Rarity> rarityMap = Cast.cast(declaredField.get(Selectors.class));
            rarityMap.put("legendary_relics:dark_gold", LRRarities.DARK_GOLD.getValue());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
