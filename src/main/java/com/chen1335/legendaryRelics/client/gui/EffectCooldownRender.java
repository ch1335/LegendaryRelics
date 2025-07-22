package com.chen1335.legendaryRelics.client.gui;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.CooldownAbleEffectType;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.client.RenderUtils;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class EffectCooldownRender implements LayeredDraw.Layer {

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        EquipmentEffectCooldownManager clientCooldownManager = LRClient.getClientCooldownManager();
        int size = clientCooldownManager.getCooldownHolders().size();
        int i = 0;
        int yAdd = size * 16;
        for (Map.Entry<EffectType<?>, EquipmentEffectCooldownManager.CooldownHolder> entry : clientCooldownManager.getCooldownHolders().entrySet()) {
            if (entry.getKey() instanceof CooldownAbleEffectType<?> effectType) {
                int width = guiGraphics.guiWidth();
                int height = guiGraphics.guiHeight();
                int x = 0;
                int y = (height + yAdd) / 2 - i * 16;
                EquipmentEffectCooldownManager.CooldownHolder cooldownHolder = entry.getValue();
                guiGraphics.blit(effectType.getCooldownIcon(), 0, y, 0, 0, 16, 16, 16, 16);
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                RenderUtils.drawSector(guiGraphics, x, y, 16, (float) cooldownHolder.getTimeLeft() / cooldownHolder.getTotalTime());
                poseStack.popPose();
                i++;
            }
        }
    }


}
