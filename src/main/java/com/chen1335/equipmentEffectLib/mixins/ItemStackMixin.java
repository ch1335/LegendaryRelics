package com.chen1335.equipmentEffectLib.mixins;

import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemStackMixin;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder, IEEItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Unique
    private int ee$mark = 0;


    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void beforeAppendLine(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        @Nullable ItemEffectsData itemEffectsData = this.get(EEItemDataComponentTypes.ITEM_EFFECT_DATA);
        ItemStack itemStack = ItemStack.class.cast(this);
        if (itemEffectsData != null) {
            itemEffectsData.effects().values().forEach(effect -> {
                effect.appendToolTip(itemStack, tooltipContext, player, tooltipFlag, list);
            });
        }
    }


    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", shift = At.Shift.AFTER))
    private void afterAppendLine(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        ItemStack itemStack = ItemStack.class.cast(this);
        SetsEffectBase setsEffect = ((IEEItemExtension) this.getItem()).EE$GetSetsEffect();
        if (setsEffect != null) {
            setsEffect.appendToolTip(itemStack, tooltipContext, player, tooltipFlag, list);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void init(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        IEEItemExtension itemExtension = (IEEItemExtension) item.asItem();
        if (EEItemDataComponentTypes.ITEM_EFFECT_DATA.isBound() && !itemExtension.EE$GetItemEffect().isEmpty()) {
            if (!components.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA.value())) {
                ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
                for (BaseEffect baseEffect : itemExtension.EE$GetItemEffect()) {
                    builder.put(baseEffect.getType(), baseEffect);
                }
                components.set(EEItemDataComponentTypes.ITEM_EFFECT_DATA.value(), new ItemEffectsData(builder.build()));
            }
        }
    }


    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void matches(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (IEEItemStackMixin.cast(stack).ee$getMark() != IEEItemStackMixin.cast(other).ee$getMark()) {
            cir.setReturnValue(false);
        }
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private ItemStack copy(ItemStack original) {
        IEEItemStackMixin.cast(original).ee$setMark(ee$getMark());
        return original;
    }

    public int ee$getMark() {
        return ee$mark;
    }

    public void ee$setMark(int flag) {
        this.ee$mark = flag;
    }
}
