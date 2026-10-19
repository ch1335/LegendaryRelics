package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import net.minecraft.world.item.*;

import java.util.List;

public class WitheringBlade extends LRMeleeWeaponBase {

    public WitheringBlade() {
        super(Tier.Weapon.WITHERING_BLADE, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.EROSION_EFFECT.value().create(1, EquipmentTypes.MAIN_HAND));
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        if (repair.is(Items.WITHER_ROSE)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
