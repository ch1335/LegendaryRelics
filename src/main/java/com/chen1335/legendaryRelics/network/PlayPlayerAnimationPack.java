package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.ClientPlayerAnimationHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** 服务端 → 客户端：让指定玩家从指定 tick 开始播放一段 PlayerAnimator 玩家动画。 */
public record PlayPlayerAnimationPack(UUID playerId, ResourceLocation animationId, int fadeTicks, int startTick) implements CustomPacketPayload {
    public static final Type<PlayPlayerAnimationPack> TYPE = new Type<>(LegendaryRelics.id("play_player_animation"));

    public static final ResourceLocation EMPTY = LegendaryRelics.id("empty");

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayPlayerAnimationPack> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayPlayerAnimationPack::playerId,
            ResourceLocation.STREAM_CODEC, PlayPlayerAnimationPack::animationId,
            ByteBufCodecs.VAR_INT, PlayPlayerAnimationPack::fadeTicks,
            ByteBufCodecs.VAR_INT, PlayPlayerAnimationPack::startTick,
            PlayPlayerAnimationPack::new
    );

    /** 从第 0 tick 开始播的便捷构造。 */
    public PlayPlayerAnimationPack(UUID playerId, ResourceLocation animationId, int fadeTicks) {
        this(playerId, animationId, fadeTicks, 0);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        // 纯客户端表现；动画层和动画注册表都只在客户端存在
        context.enqueueWork(() -> ClientPlayerAnimationHandler.handlePacket(playerId, animationId, fadeTicks, startTick));
    }
}
