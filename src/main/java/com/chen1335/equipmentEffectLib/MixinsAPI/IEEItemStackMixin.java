package com.chen1335.equipmentEffectLib.MixinsAPI;

import net.minecraft.world.item.ItemStack;

public interface IEEItemStackMixin {
    int ee$getMark();

    void ee$setMark(int flag);

    default void ee$markChanged() {
        ee$setMark(ee$getMark() + 1);
    }


    static IEEItemStackMixin cast(ItemStack itemStack) {
        return (IEEItemStackMixin) (Object) itemStack;
    }
}
