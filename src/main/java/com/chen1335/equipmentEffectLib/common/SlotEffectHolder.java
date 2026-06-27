package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import org.jetbrains.annotations.NotNull;

public record SlotEffectHolder<T extends BaseEffect>(ISlotContext context,
                                                     InfoHolder<T> infoHolder) implements Comparable<SlotEffectHolder<T>> {

    @Override
    public int compareTo(@NotNull SlotEffectHolder o) {
        return o.context.hashCode() - context.hashCode();
    }
}
