package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.equipmentEffectLib.utils.Cast;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.IntToDoubleFunction;

public class SpecialMobEffect {
    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialMobEffect> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        ByteBufCodecs.registry(RegisterTypes.SPECIAL_EFFECT_KEY).encode(buffer, value.getEffectType());
        value.encode(buffer);
    }, buffer -> {
        SpecialMobEffect object = Cast.cast(ByteBufCodecs.registry(RegisterTypes.SPECIAL_EFFECT_KEY).decode(buffer).create());
        object.decode(buffer);
        return object;
    });

    private final List<ModifierEntry> attributeModifiers = new ArrayList<>();


    private final MobEffectType<?> effectType;
    @NotNull
    private UUID sourceEntityUUID = EntityEffectData.NO_SOURCE_UUID;

    @Nullable
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

    public void setSourceEntity(@Nullable Entity entity) {
        if (entity != null) {
            this.sourceEntityUUID = entity.getUUID();
        }
    }

    protected void registerModifier(Holder<Attribute> holder, String id, AttributeModifier.Operation operation, IntToDoubleFunction function) {
        attributeModifiers.add(new ModifierEntry(holder, id, operation, function));
    }

    protected void registerModifier(Holder<Attribute> holder, ResourceLocation rl, AttributeModifier.Operation operation, IntToDoubleFunction function) {
        attributeModifiers.add(new ModifierEntry(holder, rl, operation, function));
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
        CompoundTag modifiersId = new CompoundTag();
        for (ModifierEntry attributeModifier : attributeModifiers) {
            attributeModifier.writeRl(modifiersId);
        }
        compoundTag.put("modifiersId", modifiersId);
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("SourceEntityUUID")) {
            sourceEntityUUID = compoundTag.getUUID("SourceEntityUUID");
        }
        compoundTag.getCompound("modifiersId");
        for (ModifierEntry attributeModifier : attributeModifiers) {
            attributeModifier.readRl(compoundTag);
        }
    }

    public boolean isExpired() {
        return false;
    }

    public void onAddOrUpdate(LivingEntity livingEntity) {

    }

    public void onRemove(LivingEntity livingEntity) {

    }

    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        sourceEntityUUID = buffer.readUUID();
    }

    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(sourceEntityUUID);
    }

    public String getString() {
        return "";
    }


    public float getPercentage() {
        return -1;
    }

    public void removeAttributeModifiers(AttributeMap attributeMap) {
        for (ModifierEntry attributeModifier : attributeModifiers) {
            AttributeInstance attributeinstance = attributeMap.getInstance(attributeModifier.holder);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(attributeModifier.rl);
            }
        }
    }

    public void addAttributeModifiers(AttributeMap attributeMap) {
        for (ModifierEntry attributeModifier : attributeModifiers) {
            AttributeInstance attributeinstance = attributeMap.getInstance(attributeModifier.holder);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(attributeModifier.rl);
                attributeinstance.addTransientModifier(attributeModifier.create(getModifierAmplifier()));
            }
        }
    }

    public int getModifierAmplifier() {
        return 1;
    }



    private static class ModifierEntry {
        protected final Holder<Attribute> holder;
        protected final String id;
        protected ResourceLocation rl;
        protected final AttributeModifier.Operation operation;
        protected final IntToDoubleFunction doubleFunction;

        public ModifierEntry(Holder<Attribute> holder, String id, AttributeModifier.Operation operation, IntToDoubleFunction function) {
            this.holder = holder;
            this.rl = LRUtil.randomLocation(10);
            this.id = id;
            this.operation = operation;
            this.doubleFunction = function;
        }

        public ModifierEntry(Holder<Attribute> holder, ResourceLocation rl, AttributeModifier.Operation operation, IntToDoubleFunction function) {
            this.holder = holder;
            this.rl = rl;
            this.id = rl.getNamespace();
            this.operation = operation;
            this.doubleFunction = function;
        }

        protected AttributeModifier create(int amplifier) {
            return new AttributeModifier(rl, doubleFunction.applyAsDouble(amplifier), operation);
        }

        protected void readRl(CompoundTag compoundTag) {
            rl = ResourceLocation.parse(compoundTag.getString(id));
        }

        protected void writeRl(CompoundTag compoundTag) {
            compoundTag.putString(id, rl.toString());
        }
    }
}
