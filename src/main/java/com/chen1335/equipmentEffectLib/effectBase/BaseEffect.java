package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.IEffectHelper;
import com.chen1335.equipmentEffectLib.API.ISubEffectProvider;
import com.chen1335.equipmentEffectLib.API.objects.EEItemEffectDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemStackMixin;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.legendaryRelics.registers.equipmentEffects.armorEffect.LRArmorEffect;
import com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects.LRCurioEffect;
import com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects.LRWeaponEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.*;
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
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BaseEffect implements DataComponentHolder, MutableDataComponentHolder, IEffectHelper {
    public UUID activeId = null;

    private final PatchedDataComponentMap components;

    private final EffectType<?> effectType;

    private final EquipmentType equipmentType;

    public BaseEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        this(effectType, level, equipmentType, DataComponentPatch.EMPTY);
    }

    public BaseEffect(EffectType<?> effectType, int level, EquipmentType equipmentType, DataComponentPatch dataComponentPatch) {
        this.effectType = effectType;
        this.equipmentType = equipmentType;
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL, 1);
        components = PatchedDataComponentMap.fromPatch(builder.build(), dataComponentPatch);
        components.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL.get(), level);
    }

    public static final Codec<BaseEffect> CODEC = RecordCodecBuilder.create(baseEffectInstance -> baseEffectInstance.group(
            EERegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec().fieldOf("EffectType").forGetter(BaseEffect::getType),
            EquipmentType.CODEC.optionalFieldOf("equipment_type", EquipmentType.NON).forGetter(BaseEffect::getEquipmentType),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(BaseEffect::getDataComponentPatch),
            CompoundTag.CODEC.optionalFieldOf("simpleData", new CompoundTag()).forGetter(BaseEffect::saveSimpleData)
    ).apply(baseEffectInstance, BaseEffect::buildEffect));


    public static final StreamCodec<RegistryFriendlyByteBuf, BaseEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(EERegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY),
            BaseEffect::getType,
            EquipmentType.STREAM_CODEC,
            BaseEffect::getEquipmentType,
            DataComponentPatch.STREAM_CODEC,
            BaseEffect::getDataComponentPatch,
            ByteBufCodecs.COMPOUND_TAG,
            BaseEffect::saveSimpleData,
            BaseEffect::buildEffect
    );


    private static BaseEffect buildEffect(EffectType<?> effectType, EquipmentType equipmentType, DataComponentPatch dataComponentPatch, CompoundTag simpleData) {
        BaseEffect effect;
        if (equipmentType == EquipmentType.NON) {
            effect = effectType.create(1, equipmentType);
            if (effect instanceof LRCurioEffect) {
                equipmentType = EquipmentType.CURIO;
            } else if (effect instanceof LRArmorEffect) {
                equipmentType = EquipmentType.ARMOR;
            } else if (effect instanceof LRWeaponEffect) {
                equipmentType = EquipmentType.HAND;
            }
        }
        effect = effectType.create(1, equipmentType);
        effect.applyComponents(dataComponentPatch);
        effect.loadSimpleData(simpleData);
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

    public void setRawEffectLevel(int level) {
        components.set(EEItemEffectDataComponentTypes.EFFECT_LEVEL.get(), level);
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
        BaseEffect effect = this.effectType.create(getRawEffectLevel(), getEquipmentType());
        effect.components.setAll(components.copy());
        effect.activeId = activeId;

        if (effect instanceof ISubEffectProvider copyTo && this instanceof ISubEffectProvider copyFrom) {
            copyTo.copyFrom(copyFrom);
        }
        return effect;
    }


    /**
     * 保存简单数据
     */
    public CompoundTag saveSimpleData() {
        return new CompoundTag();
    }

    /**
     * 加载简单数据
     */
    public void loadSimpleData(CompoundTag nbt) {

    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }
}
