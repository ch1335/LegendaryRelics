package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端配置同步包，双向使用：
 * <ul>
 *     <li><b>客户端 → 服务端</b>：请求把配置改成给定的值（需要权限等级 2，单人本地玩家直接放行）</li>
 *     <li><b>服务端 → 客户端</b>：把服务端当前生效的值同步过去，客户端只当作显示缓存</li>
 * </ul>
 */
public record ServerConfigPack(double zenithIceSpikeChance,
                               double zenithIceSpikeChancePerRadius) implements CustomPacketPayload {

    public static final Type<ServerConfigPack> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "server_config"));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, ServerConfigPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ServerConfigPack::zenithIceSpikeChance,
            ByteBufCodecs.DOUBLE, ServerConfigPack::zenithIceSpikeChancePerRadius,
            ServerConfigPack::new
    );

    /** 取当前生效的值，用于登录同步与修改后广播。 */
    public static ServerConfigPack ofCurrent() {
        return new ServerConfigPack(
                LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.get(),
                LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.get());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                // 客户端：只更新本地显示缓存，世界生成仍然以服务端为准
                apply();
                return;
            }
            if (!serverPlayer.hasPermissions(2) && !serverPlayer.isLocalPlayer()) {
                serverPlayer.sendSystemMessage(Component.translatable("legendary_relics.config.request_update_failure"));
                return;
            }
            apply();
            LRServerConfig.CONFIG_SPEC.save();
            serverPlayer.sendSystemMessage(Component.translatable("legendary_relics.config.request_update_success"));
            // 广播给所有人，保证各端显示一致
            for (ServerPlayer player : serverPlayer.server.getPlayerList().getPlayers()) {
                PacketDistributor.sendToPlayer(player, ofCurrent());
            }
        });
    }

    private void apply() {
        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.set(zenithIceSpikeChance);
        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.set(zenithIceSpikeChancePerRadius);
    }
}
