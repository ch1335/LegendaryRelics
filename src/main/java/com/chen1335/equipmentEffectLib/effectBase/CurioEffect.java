package com.chen1335.equipmentEffectLib.effectBase;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public class CurioEffect extends BaseEffect{
    public CurioEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public void curioTick(ItemStack itemStack, LivingEntity wearer) {

    }

    public void modifyCurioAttribute(CurioAttributeModifierEvent event) {

    }
}
