package com.chen1335.equipmentEffectLib.equipmentSources;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class CuriosSource implements IEquipmentSource {
    public static final CuriosSource INSTANCE = new CuriosSource();

    @Override
    public List<ItemStack> get(LivingEntity livingEntity) {
        ImmutableList.Builder<ItemStack> list = new ImmutableList.Builder<>();
        CuriosApi.getCuriosInventory(livingEntity).ifPresent(iCuriosItemHandler -> {
            for (int i = 0; i < iCuriosItemHandler.getEquippedCurios().getSlots(); i++) {
                ItemStack itemStack = iCuriosItemHandler.getEquippedCurios().getStackInSlot(i);
                list.add(itemStack);
            }
        });
        return list.build();
    }
}
