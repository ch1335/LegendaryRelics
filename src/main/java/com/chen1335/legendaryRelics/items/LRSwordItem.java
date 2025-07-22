package com.chen1335.legendaryRelics.items;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public abstract class LRSwordItem extends SwordItem implements IEffectEquipment {
    public LRSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }
}
