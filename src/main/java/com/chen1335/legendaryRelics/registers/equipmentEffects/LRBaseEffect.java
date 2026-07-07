package com.chen1335.legendaryRelics.registers.equipmentEffects;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LRBaseEffect extends BaseEffect {
    public LRBaseEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, BaseEffect otherEffect, ItemStack otherStack) {
        return getEffectLevel(entity, thisItemStack) > otherEffect.getEffectLevel(entity, otherStack);
    }

    @Override
    public int getEffectLevel(@Nullable LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        if (itemStack.getRarity().equals(LRRarities.DARK_GOLD.getValue())) {
            return getRawEffectLevel() + getDarkGoldLevelAdd();
        }
        return getRawEffectLevel();
    }

    public int getDarkGoldLevelAdd() {
        return 1;
    }

    public int getMaxToolTipWith(ItemStack itemStack) {
        return itemStack.getItem() instanceof ILRItemExtension extension ? extension.maxToolTipWith() : 1000;
    }
}
