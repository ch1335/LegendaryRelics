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
        PoseStack pose = guiGraphics.pose();
        RenderUtils.drawTextureWithSize(effect.getEffectType().getIcon(), guiGraphics.pose(), x, y, iconSize, iconSize, 0);

        if (effect.getPercentage() >= 0) {
            RenderUtils.drawSector(guiGraphics, x, y, iconSize, effect.getPercentage());
        }
        Font font = Minecraft.getInstance().font;
        String string = effect.getString();
        int width = font.width(string);
        guiGraphics.drawString(font, string, x + 16 - width, y + 8, 16777215, false);

        RenderSystem.disableBlend();
    }

}
