package com.chen1335.legendaryRelics.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IChargeAbleItem {
    int getMaxChargeTick(ItemStack itemStack, LivingEntity user);
}
