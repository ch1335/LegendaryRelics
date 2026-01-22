package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.equipmentEffectLib.API.IMainHandEffect;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.WeaponEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 1001)
public class ItemStackMixin {
    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void onHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> cir) {
        for (BaseEffect effect : attacker.getWeaponItem().getOrDefault(EEItemDataComponentTypes.ITEM_EFFECT_DATA, ItemEffectsData.EMPTY).effects().values()) {
            if (effect instanceof IMainHandEffect mainHandEffect) {
                mainHandEffect.hurtEnemy((ItemStack) (Object) this, target, attacker);
            }
        }
    }
}
