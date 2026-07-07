package com.chen1335.specialEffectLib.client;

import com.chen1335.legendaryRelics.client.RenderUtils;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class IconRenderer {
    public static void renderIconRow(GuiGraphics guiGraphics, int x, int y, int iconSize, SpecialMobEffect effect) {
        RenderSystem.enableBlend();
        float scale = iconSize/16F;
        RenderUtils.drawTextureWithSize(effect.getEffectType().getIcon(), guiGraphics.pose(), x, y, iconSize, iconSize, 0);

        if (effect.getPercentage() >= 0) {
            RenderUtils.drawSector(guiGraphics, x, y, iconSize, effect.getPercentage());
        }
        Font font = Minecraft.getInstance().font;
        String string = effect.getString();
        int width = font.width(string);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(iconSize - width*scale, (float) iconSize /2,10);
        pose.scale(scale,scale,1);
        guiGraphics.drawString(font, string, 0, 0, 16777215, false);
        pose.popPose();
        RenderSystem.disableBlend();
    }

}
