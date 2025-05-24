package com.chen1335.legendaryRelics.items.misc;

import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.world.item.Item;

public class DarkGold extends Item {
    public DarkGold() {
        super(new Properties().rarity(LRRarities.DARK_GOLD.getValue()));
    }
}
