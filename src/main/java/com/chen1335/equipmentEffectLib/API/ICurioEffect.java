package com.chen1335.equipmentEffectLib.API;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public interface ICurioEffect {
    default void curioTick(ItemStack itemStack, LivingEntity wearer) {

    }

    default void modifyCurioAttribute(CurioAttributeModifierEvent event) {

    }
}
