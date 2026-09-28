package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ITypeWithEntityData;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class EffectType<T extends BaseEffect> {

    private final EffectFactory<T> factory;
    private Supplier<ITypeWithEntityData<?>> dataFactory = null;
    private final boolean stackable;
    private ResourceLocation cooldownIcon = null;

    public EffectType(EffectFactory<T> factory, boolean stackable) {
        this.factory = factory;
        this.stackable = stackable;
    }

    public EffectType(EffectFactory<T> factory) {
        this(factory, false);
    }

    public EffectType(EffectFactory<T> factory, boolean stackable, Supplier<ITypeWithEntityData<?>> dataFactory) {
        this(factory, stackable);
        this.dataFactory = dataFactory;
    }

    public T create(int level, EquipmentType equipmentType) {
        return factory.create(this, level, equipmentType);
    }


    public interface EffectFactory<T extends BaseEffect> {
        T create(EffectType<T> effectType, int level, EquipmentType equipmentType);
    }


    public boolean isStackable() {
        return stackable;
    }

    /**
     * 设置冷却 HUD 显示用的图标。
     *
     * <p>传 {@code null}（默认）表示该效果不在 HUD 上显示冷却图标。
     */
    public EffectType<T> cooldownIcon(ResourceLocation cooldownIcon) {
        this.cooldownIcon = cooldownIcon;
        return this;
    }

    public ResourceLocation getCooldownIcon() {
        return cooldownIcon;
    }

    public Optional<SlotEffectHolder<T>> findBestEffect(LivingEntity living) {
        return EquipmentEffectAPI.findBestEffect(living, this);
    }


    public boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, this);
    }


    /**
     * {@link EffectType} 的链式构造器。
     *
     * <pre>{@code
     * register("backstab", EffectType.Builder.<Backstab>builder()
     *         .factory(Backstab::new)
     *         .build());
     * }</pre>
     *
     * <p>{@link #build()} 返回 {@link Supplier}，可直接交给
     * {@code DeferredRegister#register(String, Supplier)}；
     * 与 {@code MobEffectType.Builder} 的约定保持一致。
     */
    public static class Builder<T extends BaseEffect> {
        private EffectFactory<T> factory;
        private boolean stackable = false;
        private Supplier<ITypeWithEntityData<?>> dataFactory = null;
        private ResourceLocation cooldownIcon = null;

        public static <BT extends BaseEffect> Builder<BT> builder() {
            return new Builder<>();
        }

        public Builder<T> factory(EffectFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> stackable() {
            this.stackable = true;
            return this;
        }

        public Builder<T> stackable(boolean stackable) {
            this.stackable = stackable;
            return this;
        }

        public Builder<T> dataFactory(Supplier<ITypeWithEntityData<?>> dataFactory) {
            this.dataFactory = dataFactory;
            return this;
        }

        public Builder<T> cooldownIcon(ResourceLocation cooldownIcon) {
            this.cooldownIcon = cooldownIcon;
            return this;
        }

        public Supplier<EffectType<T>> build() {
            return () -> new EffectType<T>(factory, stackable, dataFactory).cooldownIcon(cooldownIcon);
        }
    }

}
