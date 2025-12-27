package com.chen1335.legendaryRelics.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;

public interface LRCurioHelper{
    default boolean isEquippedThis(LivingEntity entity) {
        Optional<ICuriosItemHandler> o = CuriosApi.getCuriosInventory(entity);
        return o.filter(iCuriosItemHandler -> !iCuriosItemHandler.findCurios((Item) this).isEmpty()).isPresent();
    }

    default ItemStack getEquippedThis(LivingEntity entity) {
        Optional<ICuriosItemHandler> o = CuriosApi.getCuriosInventory(entity);
        if (o.isPresent()) {
            List<SlotResult> list = o.get().findCurios((Item) this);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.getFirst().stack();
            }
        }
        return null;
    }
}
