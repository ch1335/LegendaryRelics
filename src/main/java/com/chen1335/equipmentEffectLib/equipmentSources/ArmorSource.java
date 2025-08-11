package com.chen1335.equipmentEffectLib.equipmentSources;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorSource implements IEquipmentSource {
    public static final ArmorSource INSTANCE = new ArmorSource();
    @Override
    public List<ItemStack> get(LivingEntity livingEntity) {
        ImmutableList.Builder<ItemStack> list = new ImmutableList.Builder<>();
        for (ItemStack armorSlot : livingEntity.getArmorSlots()) {
            list.add(armorSlot);
        }
        return list.build();
    }
}
