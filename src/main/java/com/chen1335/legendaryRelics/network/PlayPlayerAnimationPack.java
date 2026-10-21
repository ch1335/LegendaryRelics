package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.PlayerAnimationHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** 服务端 → 客户端：让指定玩家播放一段 PlayerAnimator 玩家动画。 */
public record PlayPlayerAnimationPack(UUID playerId, ResourceLocation animationId, int fadeTicks) implements CustomPacketPayload {
    public static final Type<PlayPlayerAnimationPack> TYPE = new Type<>(LegendaryRelics.id("play_player_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayPlayerAnimationPack> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayPlayerAnimationPack::playerId,
            ResourceLocation.STREAM_CODEC, PlayPlayerAnimationPack::animationId,
            ByteBufCodecs.VAR_INT, PlayPlayerAnimationPack::fadeTicks,
            PlayPlayerAnimationPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        // 纯客户端表现；动画层和动画注册表都只在客户端存在
        context.enqueueWork(() -> PlayerAnimationHandler.handlePacket(playerId, animationId, fadeTicks));
    }
}
