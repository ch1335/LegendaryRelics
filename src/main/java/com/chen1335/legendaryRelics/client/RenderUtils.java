package com.chen1335.legendaryRelics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RenderUtils {
    public static void drawSector(@NotNull GuiGraphics guiGraphics, float x, float y, float size, float percentage) {
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
        int color = FastColor.ARGB32.color(100, 255, 255, 255);


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
            case 4:
                drawSingleRectangle(guiGraphics, halfSize, 0, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                drawSingleRectangle(guiGraphics, halfSize, 90, color);
                break;
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

    public static void drawTextureWithSize(ResourceLocation atlasLocation, PoseStack poseStack, float x, float y, float width, float height, float blitOffset) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x, y, blitOffset).setUv(0, 0);
        bufferbuilder.addVertex(matrix4f, x, y + height, blitOffset).setUv(0, 1);
        bufferbuilder.addVertex(matrix4f, x + width, y + height, blitOffset).setUv(1, 1);
        bufferbuilder.addVertex(matrix4f, x + width, y, blitOffset).setUv(1, 0);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
