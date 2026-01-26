package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.IEffectHelper;
import com.chen1335.equipmentEffectLib.API.objects.EEItemEffectDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemStackMixin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BaseEffect implements DataComponentHolder, MutableDataComponentHolder, IEffectHelper {

    private final PatchedDataComponentMap components;

    private final EffectType<?> effectType;

    public BaseEffect(EffectType<?> effectType, int level) {
        this(effectType, level, DataComponentPatch.EMPTY);
    }

    public BaseEffect(EffectType<?> effectType, int level, DataComponentPatch dataComponentPatch) {
        this.effectType = effectType;
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL, 1);
        components = PatchedDataComponentMap.fromPatch(builder.build(), dataComponentPatch);
        components.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL.get(), level);
    }

    public static final Codec<BaseEffect> CODEC = RecordCodecBuilder.create(baseEffectInstance -> baseEffectInstance.group(
            EERegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec().fieldOf("EffectType").forGetter(BaseEffect::getType),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(BaseEffect::getDataComponentPatch)
    ).apply(baseEffectInstance, BaseEffect::buildEffect));


    public static final StreamCodec<RegistryFriendlyByteBuf, BaseEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(EERegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY),
            BaseEffect::getType,
            DataComponentPatch.STREAM_CODEC,
            BaseEffect::getDataComponentPatch,
            BaseEffect::buildEffect
    );

    private static BaseEffect buildEffect(EffectType<?> effectType, DataComponentPatch dataComponentPatch) {
        BaseEffect effect = effectType.create(1);
        effect.applyComponents(dataComponentPatch);
        return effect;
    }

    private DataComponentPatch getDataComponentPatch() {
        return components.asPatch();
    }

    public int getEffectLevel(@Nullable LivingEntity livingEntity, ItemStack itemStack) {
        return getRawEffectLevel();
    }

    public int getRawEffectLevel() {
        return components.getOrDefault(EEItemEffectDataComponentTypes.EFFECT_LEVEL.get(), 1);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEffect that = (BaseEffect) o;
        return Objects.equals(components, that.components) && Objects.equals(effectType, that.effectType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components, effectType);
    }

    public EffectType<? extends BaseEffect> getType() {
        return effectType;
    }

    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {

    }

    public void onActive(LivingEntity entity, ItemStack itemStack) {

    }

    public void onDeActive(LivingEntity entity, ItemStack itemStack) {

    }

    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, BaseEffect otherEffect, ItemStack otherStack) {
        return this.getEffectLevel(entity, thisItemStack) > otherEffect.getEffectLevel(entity, otherStack);
    }

    public int modifyLoot(ItemStack itemStack, LivingEntity livingTarget, LivingEntity livingAttacker, int lootingLevel) {
        return lootingLevel;
    }

    public void markChanged(ItemStack itemStack) {
        IEEItemStackMixin.cast(itemStack).ee$markChanged();
    }

    @Override
    public @NotNull DataComponentMap getComponents() {
        return this.components;
    }

    @Override
    public <T> @Nullable T set(@NotNull DataComponentType<? super T> componentType, @Nullable T value) {
        return components.set(componentType, value);
    }

    @Override
    public <T> @Nullable T remove(@NotNull DataComponentType<? extends T> componentType) {
        return components.remove(componentType);
    }

    @Override
    public void applyComponents(@NotNull DataComponentPatch patch) {
        components.applyPatch(patch);
    }

    @Override
    public void applyComponents(@NotNull DataComponentMap components) {
        this.components.setAll(components);
    }

    public BaseEffect copy() {
        BaseEffect effect = this.effectType.create(getRawEffectLevel());
        effect.components.setAll(components.copy());
        return effect;
    }
}
