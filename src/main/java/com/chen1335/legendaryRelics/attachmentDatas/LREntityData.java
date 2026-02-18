package com.chen1335.legendaryRelics.attachmentDatas;

import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.TimeLimitedAttributeBonusManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LREntityData implements INBTSerializable<CompoundTag> {
    public int reaperPickCooldown = 0;

    private final TimeLimitedAttributeBonusManager timeLimitedAttributeBonusManager = new TimeLimitedAttributeBonusManager();

    private final EquipmentEffectCooldownManager equipmentEffectCooldownManager = new EquipmentEffectCooldownManager();

    public ItemStack BowUsingArrow = ItemStack.EMPTY;

    public boolean hasGiveBook = false;

    public boolean hasGiveCharmOfFreshStart = false;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("HasGiveBook", hasGiveBook);
        compoundTag.putBoolean("HasGiveCharmOfFreshStart", hasGiveCharmOfFreshStart);
        compoundTag.put("AttributeBonusData", timeLimitedAttributeBonusManager.serializeNBT(provider));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        hasGiveBook = nbt.getBoolean("HasGiveBook");
        hasGiveCharmOfFreshStart = nbt.getBoolean("HasGiveCharmOfFreshStart");
        timeLimitedAttributeBonusManager.deserializeNBT(provider, nbt.getCompound("AttributeBonusData"));
    }

    public void tick(LivingEntity living) {
        timeLimitedAttributeBonusManager.tick(living);
        equipmentEffectCooldownManager.tick(living);
        reaperPickCooldown = Math.max(0, reaperPickCooldown - 1);
    }

    public TimeLimitedAttributeBonusManager getTimeLimitedAttributeBonusManager() {
        return timeLimitedAttributeBonusManager;
    }

    public EquipmentEffectCooldownManager getEquipmentEffectCooldownManager() {
        return equipmentEffectCooldownManager;
    }
}
