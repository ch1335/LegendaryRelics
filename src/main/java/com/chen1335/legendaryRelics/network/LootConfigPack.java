package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import net.minecraft.nbt.CompoundTag;
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

public record LootConfigPack(CompoundTag lootData) implements CustomPacketPayload {
    public static final Type<LootConfigPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "loot_config"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, LootConfigPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            LootConfigPack::lootData,
            LootConfigPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().hasPermissions(2) || context.player().isLocalPlayer()) {
                LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
                    CompoundTag compoundTag = (CompoundTag) lootData.get(id);
                    if (compoundTag != null) {
                        lootEntry.load(compoundTag);
                    }
                });
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("legendary_relics.loot_config.request_update_success"));
                    for (ServerPlayer player : serverPlayer.server.getPlayerList().getPlayers()) {
                        PacketDistributor.sendToPlayer(player, this);
                    }
                }
            } else {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("legendary_relics.loot_config.request_update_failure"));
                }
            }


        });
    }
}
