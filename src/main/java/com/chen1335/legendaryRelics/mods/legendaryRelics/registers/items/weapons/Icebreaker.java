package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.ItemAbility;

/** 破冰者：空壳，行为后续再加。 */
public class Icebreaker extends LRMeleeWeaponBase {
    public Icebreaker() {
        super(Tier.Weapon.ICEBREAKER, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return false;
    }
}
