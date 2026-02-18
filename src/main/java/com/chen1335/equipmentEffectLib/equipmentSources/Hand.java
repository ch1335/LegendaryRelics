package com.chen1335.equipmentEffectLib.equipmentSources;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Hand implements IEquipmentSource {
    public static final Hand INSTANCE = new Hand();

    @Override
    public List<ItemStack> get(LivingEntity livingEntity) {
        return List.of(livingEntity.getMainHandItem(), livingEntity.getOffhandItem());
    }
}
