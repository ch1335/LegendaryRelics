package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class SpecialMobEffect {
    private final MobEffectType<?> effectType;
    @NotNull
    private UUID sourceEntityUUID = EntityEffectData.NO_SOURCE_UUID;

    private Entity sourceEntity = null;

    private boolean sourceEntityRemoved = false;

    public SpecialMobEffect(MobEffectType<?> effectType) {
        this.effectType = effectType;
    }

    public MobEffectType<?> getEffectType() {
        return effectType;
    }

    public @NotNull UUID getSourceEntityUUID() {
        return sourceEntityUUID;
    }

    public void setSourceEntityUUID(@NotNull UUID sourceEntityUUID) {
        this.sourceEntityUUID = sourceEntityUUID;
    }

    public void setSourceEntity(@NotNull Entity entity) {
        this.sourceEntityUUID = entity.getUUID();
    }

    public @Nullable Entity getSourceEntity(Level level) {
        if (sourceEntityUUID == EntityEffectData.NO_SOURCE_UUID) {
            return null;
        } else if (sourceEntity != null) {
            if (sourceEntity.isRemoved()) {
                sourceEntityRemoved = true;
                sourceEntity = null;
                return null;
            } else {
                return sourceEntity;
            }
        } else if (sourceEntityRemoved) {
            return null;
        } else if (level.isClientSide) {
            return null;
        } else {
            sourceEntity = ((ServerLevel) level).getEntity(sourceEntityUUID);
            return sourceEntity;
        }

    }

    public void tick(LivingEntity livingEntity) {

    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putUUID("SourceEntityUUID", sourceEntityUUID);
        compoundTag.putString("EffectType", Objects.requireNonNull(RegisterTypes.SPECIAL_EFFECT_TYPE.getKey(getEffectType())).toString());
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("SourceEntityUUID")) {
            sourceEntityUUID = compoundTag.getUUID("SourceEntityUUID");
        }
    }

    public boolean isExpired() {
        return false;
    }
}
