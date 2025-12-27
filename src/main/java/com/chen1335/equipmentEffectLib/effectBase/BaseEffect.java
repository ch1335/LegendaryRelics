package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.IEffectHelper;
import com.chen1335.equipmentEffectLib.API.objects.EEItemEffectDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemStackMixin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BaseEffect implements DataComponentHolder, IEffectHelper {

    private final PatchedDataComponentMap components;

    private final EffectType<?> effectType;
    private final int effectLevel;

    public BaseEffect(EffectType<?> effectType, int level) {
        this(effectType, level, DataComponentPatch.EMPTY);
    }

    public BaseEffect(EffectType<?> effectType, int level, DataComponentPatch dataComponentPatch) {
        this.effectType = effectType;
        this.effectLevel = level;
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL, level);
        components = PatchedDataComponentMap.fromPatch(builder.build(), dataComponentPatch);
    }

    public static final Codec<BaseEffect> CODEC = RecordCodecBuilder.create(baseEffectInstance -> baseEffectInstance.group(
            RegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec().fieldOf("EffectType").forGetter(BaseEffect::getType),
            Codec.INT.fieldOf("Level").forGetter(BaseEffect::getRawEffectLevel),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(BaseEffect::getDataComponentPatch)
    ).apply(baseEffectInstance, BaseEffect::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, BaseEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY),
            BaseEffect::getType,
            ByteBufCodecs.INT,
            BaseEffect::getRawEffectLevel,
            DataComponentPatch.STREAM_CODEC,
            BaseEffect::getDataComponentPatch,
            BaseEffect::new
    );

    private DataComponentPatch getDataComponentPatch() {
        return components.asPatch();
    }

    public int getEffectLevel(@Nullable LivingEntity livingEntity, ItemStack itemStack) {
        return effectLevel;
    }

    public int getRawEffectLevel() {
        return effectLevel;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("EffectType", Objects.requireNonNull(RegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(this.getType())).toString());
        tag.putInt("Level", effectLevel);
        return tag;
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
        return this.effectLevel > otherEffect.effectLevel;
    }

    public int modifyLoot(ItemStack itemStack, LivingEntity livingTarget, LivingEntity livingAttacker, int lootingLevel) {
        return lootingLevel;
    }

    public void markItemChanged(ItemStack itemStack) {
        ((IEEItemStackMixin) (Object) itemStack).ee$setMarkFlag(!((IEEItemStackMixin) (Object) itemStack).ee$getMarkFlag());
    }

    @Override
    public @NotNull DataComponentMap getComponents() {
        return this.components;
    }
}
