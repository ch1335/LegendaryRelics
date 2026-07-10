package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ISubEffectProvider<T extends BaseEffect> {
    List<BaseEffect> getSubEffects(ItemStack itemStack);

    void copyFrom(T provider);
}
