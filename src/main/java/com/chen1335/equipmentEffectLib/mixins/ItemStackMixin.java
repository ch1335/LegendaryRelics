package com.chen1335.equipmentEffectLib.mixins;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract void enchant(Holder<Enchantment> enchantment, int level);

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", shift = At.Shift.AFTER))
    private void appendLine(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        @Nullable ItemEffectsData itemEffectsData = this.get(EEDataComponentTypes.ITEM_EFFECT_DATA);
        if (itemEffectsData != null) {
            itemEffectsData.effects().values().forEach(effect -> {
                effect.appendToolTip(ItemStack.class.cast(this), tooltipContext, player, tooltipFlag, list);
            });
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void init(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        if (item.asItem() instanceof IEffectEquipment effectEquipment) {
            if (!components.has(EEDataComponentTypes.ITEM_EFFECT_DATA.value())) {
                ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
                for (BaseEffect baseEffect : effectEquipment.EE$getDefaultEffects()) {
                    builder.put(baseEffect.getEffectType(), baseEffect);
                }
                components.set(EEDataComponentTypes.ITEM_EFFECT_DATA.value(), new ItemEffectsData(builder.build()));
            }
        }
    }
}
