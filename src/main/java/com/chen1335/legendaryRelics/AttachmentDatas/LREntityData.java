package com.chen1335.legendaryRelics.AttachmentDatas;

import com.chen1335.legendaryRelics.common.TimeLimitedAttributeBonusManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LREntityData implements INBTSerializable<CompoundTag> {
    private final TimeLimitedAttributeBonusManager timeLimitedAttributeBonusManager = new TimeLimitedAttributeBonusManager();


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {

    }

    public void tick(LivingEntity living) {
        timeLimitedAttributeBonusManager.tick(living);
    }

    public TimeLimitedAttributeBonusManager getTimeLimitedAttributeBonusManager() {
        return timeLimitedAttributeBonusManager;
    }
}
