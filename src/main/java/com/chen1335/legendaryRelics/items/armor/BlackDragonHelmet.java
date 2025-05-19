package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import net.minecraft.world.item.Rarity;

public class BlackDragonHelmet extends BlackDragonArmor {

    public BlackDragonHelmet() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.HELMET, new Properties().rarity(Rarity.EPIC));
    }
}
