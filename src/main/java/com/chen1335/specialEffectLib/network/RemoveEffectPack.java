package com.chen1335.specialEffectLib.network;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
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

import java.util.UUID;

/**
 * 移除某个来源施加的特殊效果，客户端收到后同步删除。
 */
public record RemoveEffectPack(int id, UUID source, MobEffectType<?> effectType) implements CustomPacketPayload {

    public static final Type<RemoveEffectPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SpecialEffectLib.MODID, "remove_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveEffectPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RemoveEffectPack::id,
            StreamCodec.of((buffer, value) -> buffer.writeUUID(value), buffer -> buffer.readUUID()),
            RemoveEffectPack::source,
            ByteBufCodecs.registry(RegisterTypes.SPECIAL_EFFECT_KEY),
            RemoveEffectPack::effectType,
            RemoveEffectPack::new
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
                living.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA).removeEffect(source, effectType);
            }
        }
    }
}
