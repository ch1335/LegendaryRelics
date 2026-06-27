package com.chen1335.legendaryRelics.registers.attachmentDatas;

import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.common.EffectInfoHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Optional;

public class LRProjectileData implements INBTSerializable<CompoundTag> {
    public final EffectInfoHolder attachedEffects = new EffectInfoHolder(new HashMap<>());

    public float damageMul = 1;

    public boolean ignoreHitCooldown = false;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("attachedEffects", attachedEffects.serializeNBT(registryOps));
        compoundTag.putFloat("damageMul", damageMul);
        compoundTag.putBoolean("ignoreHitCooldown", ignoreHitCooldown);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
        attachedEffects.deserializeNBT(registryOps, nbt.get("attachedEffects"));
        damageMul = nbt.getFloat("damageMul");
        ignoreHitCooldown = nbt.getBoolean("ignoreHitCooldown");
    }

    public void attachEffect(InfoHolder<? extends BaseEffect> infoHolder) {
        attachedEffects.effectMap().put(infoHolder.effect().getType(), infoHolder);
    }


    public <T extends BaseEffect> Optional<InfoHolder<T>> getEffect(EffectType<T> effectType) {
        return Optional.ofNullable(attachedEffects.get(effectType));
    }

}
