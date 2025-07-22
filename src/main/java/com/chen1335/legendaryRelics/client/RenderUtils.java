package com.chen1335.legendaryRelics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderUtils {
    public static void drawSector(@NotNull GuiGraphics guiGraphics, float x, float y,float size, float percentage) {
        PoseStack poseStack = guiGraphics.pose();
        float halfSize = size / 2;
        int p = (int) (percentage * 100);
        int i = p / 25;
        int a = p % 25;
        float b = 0;
        float c = 0;
        if (a > 12.5) {
            b = (float) (halfSize * (a - 25) / 12.5);

        } else {
            c = (float) (halfSize * (a - 12.5) / 12.5);
            b = -halfSize;
        }
        int color = Integer.MAX_VALUE;


        float centerX = x + halfSize;
        float centerY = y + halfSize;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        switch (i) {
            case 3:
                poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                break;
            case 2:
                poseStack.mulPose(Axis.ZN.rotationDegrees(0));
                break;
            case 1:
                poseStack.mulPose(Axis.ZN.rotationDegrees(-90));
                break;
            case 0:
                poseStack.mulPose(Axis.ZN.rotationDegrees(-180));
                break;
        }
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        vertexConsumer.addVertex(matrix4f, 0, 0, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, 0, halfSize, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, halfSize + c, halfSize, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, halfSize + c, 0 - b, 0).setColor(color);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        switch (i) {
            case 3:
                drawSingleRectangle(guiGraphics, halfSize, 0, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                break;
            case 2:
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                break;
            case 1:
                drawSingleRectangle(guiGraphics, halfSize, 180, color);
                break;
            case 0:
                break;
        }
        poseStack.popPose();

        RenderSystem.disableBlend();
        guiGraphics.flush();
    }

    private static void drawSingleRectangle(@NotNull GuiGraphics guiGraphics, float size, float degrees, int color) {
        guiGraphics.pose().mulPose(Axis.ZN.rotationDegrees(-degrees));
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        vertexConsumer.addVertex(matrix4f, 0, 0, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, 0, size, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, size, size, 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, size, 0, 0).setColor(color);
    }
}
