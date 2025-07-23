package com.chen1335.equipmentEffectLib.MixinsAPI;

import net.minecraft.world.item.ItemStack;

public interface IEEItemStackMixin {
    boolean ee$getMarkFlag();

    void ee$setMarkFlag(boolean flag);

    static IEEItemStackMixin cast(ItemStack itemStack) {
        return (IEEItemStackMixin) (Object) itemStack;
    }
}
