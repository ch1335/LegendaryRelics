package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.common.LRSimpleTier;
import net.minecraft.world.item.SwordItem;

public abstract class LRSwordItem extends SwordItem implements IEffectEquipment, ILRItemExtension {
    public LRSwordItem(LRSimpleTier tier, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 0, tier.getAttackSpeed())));
    }
}
