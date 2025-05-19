package com.chen1335.legendaryRelics.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public interface LRCurioHelper {
    default boolean isEquippedThis(LivingEntity entity) {
        Optional<ICuriosItemHandler> o = CuriosApi.getCuriosInventory(entity);
        return o.filter(iCuriosItemHandler -> !iCuriosItemHandler.findCurios((Item) this).isEmpty()).isPresent();
    }
}
