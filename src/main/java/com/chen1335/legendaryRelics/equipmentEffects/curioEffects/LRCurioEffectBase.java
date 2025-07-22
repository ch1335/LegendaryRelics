package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.CurioEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class LRCurioEffectBase extends CurioEffect {
    public LRCurioEffectBase(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    @Override
    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, BaseEffect otherEffect, ItemStack otherStack) {
        return getEffectLevel(entity, thisItemStack) > otherEffect.getEffectLevel(entity, otherStack);
    }

    @Override
    public int getEffectLevel(@Nullable LivingEntity livingEntity, ItemStack itemStack) {
        if (itemStack.getRarity().equals(LRRarities.DARK_GOLD.getValue())) {
            return getRawEffectLevel() + getDarkGoldLevelAdd();
        }
        return getRawEffectLevel();
    }

    public int getDarkGoldLevelAdd() {
        return 1;
    }
}
