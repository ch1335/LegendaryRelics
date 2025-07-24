package com.chen1335.legendaryRelics.client.gui;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.CooldownAbleEffectType;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.client.RenderUtils;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.config.Config;
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
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        float xPercentage = (float) Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X;
        float yPercentage = (float) Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y;
        int x = (int) (width * xPercentage);
        int y = (int) (height * yPercentage)-16;
        for (Map.Entry<EffectType<?>, EquipmentEffectCooldownManager.CooldownHolder> entry : clientCooldownManager.getCooldownHolders().entrySet()) {
            if (entry.getKey() instanceof CooldownAbleEffectType<?> effectType) {
                y = y + 16;
                EquipmentEffectCooldownManager.CooldownHolder cooldownHolder = entry.getValue();
                guiGraphics.blit(effectType.getCooldownIcon(), x, y, 0, 0, 16, 16, 16, 16);
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                RenderUtils.drawSector(guiGraphics, x, y, 16, (float) cooldownHolder.getTimeLeft() / cooldownHolder.getTotalTime());
                poseStack.popPose();
            }
        }
    }


}
