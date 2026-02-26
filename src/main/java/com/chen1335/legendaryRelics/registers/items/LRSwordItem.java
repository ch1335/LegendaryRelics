package com.chen1335.legendaryRelics.registers.items;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public abstract class LRSwordItem extends SwordItem implements IEffectEquipment , ILRItemExtension {
    public LRSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }
}
