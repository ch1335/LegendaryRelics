package com.chen1335.equipmentEffectLib.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IArmorEffect {
    default void armorTick(ItemStack itemStack, LivingEntity wearer) {

    }
}
