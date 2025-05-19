package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import net.minecraft.world.item.Rarity;

public class BlackDragonChestPlate extends BlackDragonArmor {
    public BlackDragonChestPlate() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC));
    }
}
