package com.chen1335.legendaryRelics.registers.items.weapons;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.registers.items.LRSwordItem;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WitheringBlade extends LRSwordItem {

    public WitheringBlade() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 6, -2.4F)));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.EROSION_EFFECT.value().create(1, EquipmentType.HAND));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        if (repair.is(Items.WITHER_ROSE)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
