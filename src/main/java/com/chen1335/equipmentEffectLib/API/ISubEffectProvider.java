package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ISubEffectProvider {
    List<BaseEffect> getSubEffects(ItemStack itemStack);
}
