package com.chen1335.equipmentEffectLib.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IEquipmentSource {
    List<ItemStack> get(LivingEntity livingEntity);
}
