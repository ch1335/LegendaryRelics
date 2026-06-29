package com.chen1335.specialEffectLib.client;

import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecialEffectIconRender implements LayeredDraw.Layer {

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui) return;
        Gui gui = minecraft.gui;

        EntityEffectData effectData = SpecialEffectAPI.getEntityEffectData(player);
        List<SpecialMobEffect> beneficialIcons = new ArrayList<>();
        List<SpecialMobEffect> harmfulIcons = new ArrayList<>();

        for (Map<MobEffectType<?>, SpecialMobEffect> effectMap : effectData.getEffects().values()) {
            for (Map.Entry<MobEffectType<?>, SpecialMobEffect> entry : effectMap.entrySet()) {
                SpecialMobEffect effect = entry.getValue();

                if (effect.getEffectType().getCategory() == MobEffectCategory.HARMFUL) {
                    harmfulIcons.add(effect);
                } else {
                    beneficialIcons.add(effect);
                }
            }
        }

        int guiWidth = guiGraphics.guiWidth();
        int guiHeight = guiGraphics.guiHeight();

        if (!beneficialIcons.isEmpty()) {
            renderIcons(guiGraphics, guiWidth, guiHeight - gui.leftHeight, beneficialIcons, -1);
        }

        if (!harmfulIcons.isEmpty()) {
            renderIcons(guiGraphics, guiWidth, guiHeight - gui.rightHeight, harmfulIcons, 1);
        }
    }


    private static void renderIcons(GuiGraphics guiGraphics, int guiWidth, int y, List<SpecialMobEffect> effects, int i) {
        int iconSize = 16;
        float scale = 0.6F;
        int x = guiWidth / 2 + (91 * i);
        RenderSystem.enableBlend();
        PoseStack pose = guiGraphics.pose();
        for (SpecialMobEffect effect : effects) {
            pose.pushPose();
            pose.translate(x, y + 9 - iconSize * scale, 0);
            pose.scale(scale, scale, 0);
            IconRenderer.renderIconRow(guiGraphics, 0, 0, iconSize, effect);
            x = (int) (x + (iconSize ) * -i * scale)+2;
            pose.popPose();
        }
        RenderSystem.disableBlend();
    }
}
