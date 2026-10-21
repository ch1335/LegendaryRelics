package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.network.PlayPlayerAnimationPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerAnimationHandler {
    /** 从第 0 tick 开始播放。 */
    public static void play(Player player, ResourceLocation resourceLocation, int fadeTicks) {
        playFrom(player, resourceLocation, 0, fadeTicks);
    }

    /** 从动画的第 startTick 个 tick 开始播放（20 tick = 1 秒）。 */
    public static void playFrom(Player player, ResourceLocation resourceLocation, int startTick, int fadeTicks) {
        if (!player.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                    new PlayPlayerAnimationPack(player.getUUID(), resourceLocation, fadeTicks, startTick));
        }
    }

    public static void clean(Player player, int fadeTicks) {
        if (!player.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                    new PlayPlayerAnimationPack(player.getUUID(), PlayPlayerAnimationPack.EMPTY, fadeTicks, 0));
        }
    }
}
