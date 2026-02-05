package com.chen1335.specialEffectLib.network;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AddOrUpdateEffectPack(int id, SpecialMobEffect specialMobEffect) implements CustomPacketPayload {
    public static final Type<AddOrUpdateEffectPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpecialEffectLib.MODID, "add_or_update_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddOrUpdateEffectPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AddOrUpdateEffectPack::id,
            SpecialMobEffect.STREAM_CODEC,
            AddOrUpdateEffectPack::specialMobEffect,
            AddOrUpdateEffectPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        Level level = context.player().level();
        if (level.isClientSide) {
            Entity entity = level.getEntity(id);
            if (entity instanceof LivingEntity living) {
                living.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).putEffect(specialMobEffect.getEffectType(), specialMobEffect);
            }
        }
    }
}
