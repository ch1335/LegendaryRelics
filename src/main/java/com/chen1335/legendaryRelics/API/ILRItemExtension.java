package com.chen1335.legendaryRelics.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ILRItemExtension {
    default boolean isNoRightClickCooldown() {
        return false;
    }

    default void onEquipmentChangeFrom(ItemStack from, ItemStack to, LivingEntity entity) {

    }


    default void onEquipmentChangeTo(ItemStack from, ItemStack to, LivingEntity entity) {

    }

    default int maxToolTipWidth(ItemStack itemStack) {
        return defaultMaxToolTipWidth(itemStack);
    }

    static int defaultMaxToolTipWidth(ItemStack itemStack) {
        return 270;
    }
}
