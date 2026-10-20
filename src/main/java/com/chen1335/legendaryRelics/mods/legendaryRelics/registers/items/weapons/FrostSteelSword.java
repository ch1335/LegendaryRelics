package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import net.minecraft.world.item.Rarity;

/** 霜钢剑：标准剑类武器，暂无特殊行为。 */
public class FrostSteelSword extends LRMeleeWeaponBase {
    public FrostSteelSword() {
        super(Tier.Weapon.FROST_STEEL_SWORD, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
