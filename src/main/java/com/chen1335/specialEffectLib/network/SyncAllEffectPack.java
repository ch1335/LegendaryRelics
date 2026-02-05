package com.chen1335.specialEffectLib.network;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
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

public record SyncAllEffectPack(int entityId, EntityEffectData entityEffectData) implements CustomPacketPayload {
    public static final Type<SyncAllEffectPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpecialEffectLib.MODID, "sync_all_effect"));


    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAllEffectPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SyncAllEffectPack::entityId,
            EntityEffectData.STREAM_CODEC,
            SyncAllEffectPack::entityEffectData,
            SyncAllEffectPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        Level level = context.player().level();
        if (level.isClientSide) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity living) {
                living.setData(SEAttachmentTypes.ENTITY_EFFECT_DATA, entityEffectData);
            }
        }
    }
}
