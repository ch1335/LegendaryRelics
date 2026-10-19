package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRSwordItem;
import net.minecraft.world.item.Rarity;

/** 烈焰飞轮：空壳，行为后续再加。 */
public class BlazingChakram extends LRSwordItem {
    public BlazingChakram() {
        super(Tier.Weapon.BLAZING_CHAKRAM, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
