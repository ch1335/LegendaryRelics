package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.BlackDragonChestPlateEffect;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class BlackDragonChestPlate extends BlackDragonArmor {
    public BlackDragonChestPlate() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public List<BaseEffect> EE$getDefaultEffects() {
        return List.of(new BlackDragonChestPlateEffect(2));
    }

}
