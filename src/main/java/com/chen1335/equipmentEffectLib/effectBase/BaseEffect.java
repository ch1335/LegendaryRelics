package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.IEffectHelper;
import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemStackMixin;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BaseEffect implements IEffectHelper {

    private final EffectType<?> effectType;
    private final int effectLevel;
    private CompoundTag cachedData = new CompoundTag();

    public BaseEffect(EffectType<?> effectType, int level) {
        this.effectType = effectType;
        this.effectLevel = level;
    }

    public static final Codec<BaseEffect> CODEC = CompoundTag.CODEC.xmap(BaseEffect::loadFromNbt, BaseEffect::save);


    public static final StreamCodec<ByteBuf, BaseEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            BaseEffect::save,
            BaseEffect::loadFromNbt
    );

    public int getEffectLevel(@Nullable LivingEntity livingEntity, ItemStack itemStack) {
        return effectLevel;
    }

    public int getRawEffectLevel() {
        return effectLevel;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("EffectType", Objects.requireNonNull(RegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(getEffectType())).toString());
        tag.putInt("Level", effectLevel);
        return tag;
    }

    public void load(CompoundTag tag) {

    }

    public static BaseEffect loadFromNbt(CompoundTag tag) {
        String effectTypeId = tag.getString("EffectType");
        int level = tag.getInt("Level");

        EffectType<?> effectType = RegisterTypes.EQUIPMENT_EFFECT_TYPE.get(ResourceLocation.parse(effectTypeId));
        if (effectType == null) {
            return null;
        } else {
            BaseEffect baseEffect = effectType.create(level);
            baseEffect.load(tag);
            baseEffect.cachedData = tag;
            return baseEffect;
        }
    }

    @Override
    public int hashCode() {
        return cachedData.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BaseEffect baseEffect) {
            if (!this.getClass().equals(other.getClass())) {
                return false;
            }

            return this.save().equals(baseEffect.save());
        } else {
            return false;
        }
    }


    @Override
    public EffectType<?> getEffectType() {
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

}
