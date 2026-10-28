package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.ShadowStrike;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端按下暗影突袭键后，把准星指向的目标发给服务端。
 */
public record ShadowStrikePack(int targetId) implements CustomPacketPayload {

    public static final Type<ShadowStrikePack> TYPE = new Type<>(LegendaryRelics.id("shadow_strike"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShadowStrikePack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ShadowStrikePack::targetId,
            ShadowStrikePack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                Entity target = player.level().getEntity(this.targetId);
                ShadowStrike.tryActivate(player, target);
            }
        });
    }
}
