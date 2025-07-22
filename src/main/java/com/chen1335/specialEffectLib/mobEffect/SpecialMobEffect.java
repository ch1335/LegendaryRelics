package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.legendaryRelics.specialMobEffects.Erosion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpecialMobEffect {
    private final MobEffectType<?> effectType;
    @Nullable
    private UUID sourceEntityUUID = null;

    private Entity sourceEntity = null;

    private boolean sourceEntityRemoved = false;

    public SpecialMobEffect(MobEffectType<?> effectType) {
        this.effectType = effectType;
    }

    public MobEffectType<?> getEffectType() {
        return effectType;
    }

    public @Nullable UUID getSourceEntityUUID() {
        return sourceEntityUUID;
    }

    public void setSourceEntityUUID(@Nullable UUID sourceEntityUUID) {
        this.sourceEntityUUID = sourceEntityUUID;
    }

    public void setSourceEntity(@NotNull Entity entity) {
        this.sourceEntityUUID = entity.getUUID();
    }

    public @Nullable Entity getSourceEntity(Level level) {
        if (sourceEntityUUID == null) {
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
            if (sourceEntityUUID == null) {
                return null;
            }
            sourceEntity = ((ServerLevel) level).getEntity(sourceEntityUUID);
            return sourceEntity;
        }

    }

    public void tick(LivingEntity livingEntity) {

    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        if (sourceEntityUUID != null) {
            compoundTag.putUUID("SourceEntityUUID", sourceEntityUUID);
        }
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
