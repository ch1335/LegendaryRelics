package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.network.PlayPlayerAnimationPack;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.UUID;

/**
 * 玩家动画（PlayerAnimator）：注册动画层 + 播放。
 * <p>服务端只发 {@link com.chen1335.legendaryRelics.network.PlayPlayerAnimationPack}，动画本身纯客户端。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID, value = Dist.CLIENT)
public final class ClientPlayerAnimationHandler {
    /** 我们自己的动画层 id，避免和其它模组抢层。 */
    public static final ResourceLocation LAYER_ID = LegendaryRelics.id("weapon_animation");
    /** 1500+ 留给玩法动画，压过表情一类的表现层。 */
    private static final int LAYER_PRIORITY = 1500;

    private ClientPlayerAnimationHandler() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 工厂会对每个客户端玩家各调用一次，返回的层会自动进动画栈并按 LAYER_ID 存回玩家数据
        event.enqueueWork(() -> PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                LAYER_ID, LAYER_PRIORITY, player -> new ModifierLayer<IAnimation>()));
    }

    /** 网络包入口：按 uuid 找玩家再播。 */
    public static void handlePacket(UUID playerId, ResourceLocation animationId, int fadeTicks, int startTick) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (Minecraft.getInstance().level.getPlayerByUUID(playerId) instanceof AbstractClientPlayer player) {
            play(player, animationId, fadeTicks, startTick);
        }
    }

    public static void play(AbstractClientPlayer player, ResourceLocation animationId, int fadeTicks) {
        play(player, animationId, fadeTicks, 0);
    }

    @SuppressWarnings("unchecked")
    public static void play(AbstractClientPlayer player, ResourceLocation animationId, int fadeTicks, int startTick) {
        if (!(PlayerAnimationAccess.getPlayerAssociatedData(player).get(LAYER_ID) instanceof ModifierLayer<?> stored)) {
            LegendaryRelics.LOGGER.warn("玩家 {} 没有注册动画层，跳过 {}", player.getUUID(), animationId);
            return;
        }
        if (animationId.equals(PlayPlayerAnimationPack.EMPTY)) {
            // 传 null 让当前动画淡出并清空该层；层本身保留，后续还能继续播
            ((ModifierLayer<IAnimation>) stored).replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(Math.max(1, fadeTicks), Ease.LINEAR), null);
            return;
        }
        IPlayable animation = PlayerAnimationRegistry.getAnimation(animationId);
        if (!(animation instanceof KeyframeAnimation data)) {
            LegendaryRelics.LOGGER.warn("找不到玩家动画 {}（检查 assets/<modid>/player_animations/ 与动画内的 name）", animationId);
            return;
        }
        // 起始 tick 夹到有效范围，避免直接越过 stopTick 导致第一 tick 就停掉
        int tick = Mth.clamp(startTick, 0, Math.max(0, data.stopTick - 1));
        KeyframeAnimationPlayer animationPlayer = new KeyframeAnimationPlayer(data, tick);
        // 第三人称模型在第一人称下也要显示，否则自己看不到手臂动作
        animationPlayer.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
        ((ModifierLayer<IAnimation>) stored).replaceAnimationWithFade(
                AbstractFadeModifier.standardFadeIn(Math.max(1, fadeTicks), Ease.LINEAR), animationPlayer);
    }
}
