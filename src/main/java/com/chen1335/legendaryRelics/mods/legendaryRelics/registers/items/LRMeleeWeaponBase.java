package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.common.LRSimpleTier;
import net.minecraft.world.item.SwordItem;

public abstract class LRMeleeWeaponBase extends SwordItem implements IEffectEquipment, ILRItemExtension {
    public LRMeleeWeaponBase(LRSimpleTier tier, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 0, tier.getAttackSpeed())));
    }
}
