package com.chen1335.equipmentEffectLib.kubejs;

import com.chen1335.equipmentEffectLib.API.IArmorEffect;
import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.API.IMainHandEffect;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BaseEffectJS extends BaseEffect {
    TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onActive = (baseEffect, living, itemStack) -> {
    };
    TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onDeActive = (baseEffect, living, itemStack) -> {
    };

    Consumer<EquipmentEffectBuilder.AppendToolTipContext> appendToolTip = context -> {
    };

    CompoundTag getNbt() {
        if (!has(DataComponents.CUSTOM_DATA)) {
            set(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag()));
        }
        return get(DataComponents.CUSTOM_DATA).getUnsafe();
    }

    public BaseEffectJS(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        onActive.accept(this, entity, itemStack);
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        onDeActive.accept(this, entity, itemStack);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        appendToolTip.accept(new EquipmentEffectBuilder.AppendToolTipContext(this, itemStack, context, player, tooltipFlag, tooltipComponents));
    }


    public static class CurioEffectJS extends BaseEffectJS implements ICurioEffect {
        TriConsumer<CurioEffectJS, ItemStack, LivingEntity> curioTick = (curioEffectJS, itemStack, living) -> {
        };
        BiConsumer<CurioEffectJS, CurioAttributeModifierEvent> modifyCurioAttribute = (curioEffectJS, event) -> {
        };

        public CurioEffectJS(EffectType<?> effectType, int level) {
            super(effectType, level);
        }

        @Override
        public void curioTick(ItemStack itemStack, LivingEntity wearer) {
            curioTick.accept(this, itemStack, wearer);
        }

        @Override
        public void modifyCurioAttribute(CurioAttributeModifierEvent event) {
            modifyCurioAttribute.accept(this, event);
        }
    }

    public static class ArmorEffectJS extends BaseEffectJS implements IArmorEffect {

        public ArmorEffectJS(EffectType<?> effectType, int level) {
            super(effectType, level);
        }

    }

    public static class MainHandEffectJS extends BaseEffectJS implements IMainHandEffect {
        Consumer<EquipmentEffectBuilder.MainHandEffectBuilder.HurtEnemyContext> hurtEnemy = hurtEnemyContext -> {
        };

        @Override
        public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
            hurtEnemy.accept(new EquipmentEffectBuilder.MainHandEffectBuilder.HurtEnemyContext(this, stack, target, attacker));
        }

        public MainHandEffectJS(EffectType<?> effectType, int level) {
            super(effectType, level);
        }

    }
}
