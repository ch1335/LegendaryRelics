package com.chen1335.legendaryRelics.items.weapons;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.ErosionEffect;
import com.chen1335.legendaryRelics.items.LRSwordItem;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WitheringBlade extends LRSwordItem {

    public WitheringBlade() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 6, -2.4F)));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new ErosionEffect(1));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        if (repair.is(Items.WITHER_ROSE)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
