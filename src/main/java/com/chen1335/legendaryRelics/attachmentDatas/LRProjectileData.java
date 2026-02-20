package com.chen1335.legendaryRelics.attachmentDatas;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class LRProjectileData implements INBTSerializable<CompoundTag> {
    public Entity lastHitEntity = null;

    public int pierceLevel = 1;

    public float damageMul = 1;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("pierceLevel", pierceLevel);
        compoundTag.putFloat("damageMul", damageMul);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        pierceLevel = nbt.getInt("pierceLevel");
        damageMul = nbt.getFloat("damageMul");
    }


}
