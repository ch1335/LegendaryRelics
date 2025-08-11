package com.chen1335.equipmentEffectLib.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IEquipmentSource {
    public List<ItemStack> get(LivingEntity livingEntity);
}
