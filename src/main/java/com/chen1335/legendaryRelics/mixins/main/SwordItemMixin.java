package com.chen1335.legendaryRelics.mixins.main;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.ErosionEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(SwordItem.class)
public class SwordItemMixin implements IEffectEquipment {
    @Override
    public List<BaseEffect> EE$getDefaultEffects() {
        if ((Object) this == Items.NETHERITE_SWORD) {
            return List.of(new ErosionEffect(1));
        }
        return IEffectEquipment.super.EE$getDefaultEffects();
    }
}
