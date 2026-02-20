package com.chen1335.equipmentEffectLib.kubejs;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.CooldownAbleEffectType;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.util.Cast;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class EquipmentEffectBuilder<T extends EquipmentEffectBuilder<?>> extends BuilderBase<EffectType<?>> {
    public record AppendToolTipContext(BaseEffectJS baseEffectJS, ItemStack itemStack, Item.TooltipContext context,
                                       Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {

    }

    protected TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onActive = (baseEffectJS, living, itemStack) -> {
    };
    protected TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onDeActive = (baseEffectJS, living, itemStack) -> {
    };
    protected Consumer<AppendToolTipContext> appendToolTip = context -> {
    };
    protected boolean cooldownAble = false;
    protected ResourceLocation cooldownIcon = null;

    public EquipmentEffectBuilder(ResourceLocation id) {
        super(id);
    }

    protected EquipmentType equipmentType = EquipmentType.ALL;

    public T effectEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        return Cast.cast(this);
    }

    public T onActive(TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onActive) {
        this.onActive = onActive;
        return Cast.cast(this);
    }


    public T onDeActive(TriConsumer<BaseEffectJS, LivingEntity, ItemStack> onDeActive) {
        this.onDeActive = onDeActive;
        return Cast.cast(this);
    }


    public T appendToolTip(Consumer<AppendToolTipContext> appendToolTip) {
        this.appendToolTip = appendToolTip;
        return Cast.cast(this);
    }

    public T cooldownAble(ResourceLocation icon) {
        this.cooldownAble = true;
        this.cooldownIcon = icon;
        return Cast.cast(this);
    }

    protected <B extends BaseEffectJS> B createBase(B baseEffect) {
        baseEffect.onActive = this.onActive;
        baseEffect.onDeActive = this.onDeActive;
        baseEffect.appendToolTip = this.appendToolTip;
        return baseEffect;
    }

    public static class CurioEffectBuilder extends EquipmentEffectBuilder<CurioEffectBuilder> {
        TriConsumer<BaseEffectJS.CurioEffectJS, ItemStack, LivingEntity> curioTick = (curioEffectJS, itemStack, living) -> {
        };
        BiConsumer<BaseEffectJS.CurioEffectJS, CurioAttributeModifierEvent> modifyCurioAttribute = (curioEffectJS, event) -> {
        };

        public CurioEffectBuilder(ResourceLocation id) {
            super(id);
        }

        public CurioEffectBuilder curioTick(TriConsumer<BaseEffectJS.CurioEffectJS, ItemStack, LivingEntity> consumer) {
            curioTick = consumer;
            return this;
        }

        public CurioEffectBuilder modifyCurioAttribute(BiConsumer<BaseEffectJS.CurioEffectJS, CurioAttributeModifierEvent> consumer) {
            modifyCurioAttribute = consumer;
            return this;
        }

        @Override
        public EffectType<?> createObject() {
            EffectType.EffectFactory<BaseEffectJS.CurioEffectJS> factory = (effectType, level, equipmentType) -> {
                BaseEffectJS.CurioEffectJS baseEffect = createBase(new BaseEffectJS.CurioEffectJS(effectType, level, equipmentType));
                baseEffect.curioTick = this.curioTick;
                baseEffect.modifyCurioAttribute = this.modifyCurioAttribute;
                return baseEffect;
            };

            if (cooldownAble) {
                return new CooldownAbleEffectType<>(factory).cooldownIcon(cooldownIcon);
            } else {
                return new EffectType<>(factory);
            }
        }
    }

    public static class ArmorEffectBuilder extends EquipmentEffectBuilder<ArmorEffectBuilder> {

        public ArmorEffectBuilder(ResourceLocation id) {
            super(id);
        }


        @Override
        public EffectType<?> createObject() {
            EffectType.EffectFactory<BaseEffectJS.ArmorEffectJS> factory = (effectType, level, equipmentType) -> createBase(new BaseEffectJS.ArmorEffectJS(effectType, level, equipmentType));
            if (cooldownAble) {
                return new CooldownAbleEffectType<>(factory).cooldownIcon(cooldownIcon);
            } else {
                return new EffectType<>(factory);
            }
        }
    }

    public static class MainHandEffectBuilder extends EquipmentEffectBuilder<MainHandEffectBuilder> {
        public record HurtEnemyContext(BaseEffectJS.MainHandEffectJS mainHandEffectJS, @NotNull ItemStack stack,
                                       @NotNull LivingEntity target, @NotNull LivingEntity attacker) {

        }

        Consumer<HurtEnemyContext> hurtEnemy = hurtEnemyContext -> {

        };

        public MainHandEffectBuilder(ResourceLocation id) {
            super(id);
        }

        public MainHandEffectBuilder hurtEnemy(Consumer<HurtEnemyContext> consumer) {
            hurtEnemy = consumer;
            return this;
        }

        @Override
        public EffectType<?> createObject() {
            EffectType.EffectFactory<BaseEffectJS.MainHandEffectJS> factory = (effectType, level, equipmentType) -> {
                BaseEffectJS.MainHandEffectJS baseEffect = createBase(new BaseEffectJS.MainHandEffectJS(effectType, level, equipmentType));
                baseEffect.hurtEnemy = this.hurtEnemy;
                return baseEffect;
            };

            if (cooldownAble) {
                return new CooldownAbleEffectType<>(factory).cooldownIcon(cooldownIcon);
            } else {
                return new EffectType<>(factory);
            }
        }
    }
}
