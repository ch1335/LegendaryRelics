package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import net.minecraft.world.item.Rarity;

public class BlackDragonLeggings extends BlackDragonArmor {
    public BlackDragonLeggings() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC));
    }
}
