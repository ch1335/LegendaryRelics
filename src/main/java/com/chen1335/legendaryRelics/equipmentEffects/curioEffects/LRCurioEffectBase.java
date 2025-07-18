package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class LRCurioEffectBase extends BaseEffect {
    public LRCurioEffectBase(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    @Override
    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, Pair<ItemStack, BaseEffect> oldPair) {
        return getEffectLevel(entity, thisItemStack) > oldPair.getSecond().getEffectLevel(entity, oldPair.getFirst());
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
