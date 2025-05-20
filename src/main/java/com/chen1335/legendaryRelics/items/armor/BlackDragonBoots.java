package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import net.minecraft.world.item.Rarity;

public class BlackDragonBoots extends BlackDragonArmor {
    public BlackDragonBoots() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.BOOTS, new Properties().rarity(Rarity.EPIC));
    }
}
