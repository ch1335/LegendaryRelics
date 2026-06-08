package com.chen1335.legendaryRelics.client.renderUtils.trail;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProjectileTrail {
    private final Supplier<Boolean> needRemove;
    private final Function<Float, Vec3> position;
    private final int maxLength;
    private final float thickness;
    private final int rgbaColor;
    private final float fadeStartRatio;
    public List<TrailNode> trailNodes = new ArrayList<>();

    public int maxLife = 400;

    public boolean needRemove() {
        return trailNodes.isEmpty() || maxLife < 0;
    }

    public Vec3 getPosition(float partialTick) {
        return position.apply(partialTick);
    }

    public ProjectileTrail(Supplier<Boolean> needRemove, Function<Float, Vec3> position, int maxLength, float thickness, int rgbaColor, float fadeStartRatio) {
        this.needRemove = needRemove;
        this.position = position;
        this.maxLength = maxLength;
        this.thickness = thickness;
        this.rgbaColor = rgbaColor;
        this.fadeStartRatio = Math.max(0.0F, Math.min(1.0F, fadeStartRatio));
    }

    public void updatePos(Vec3 position) {
        if (!needRemove.get()) {
            trailNodes.add(new TrailNode(position));
            if (trailNodes.size() >= maxLength) {
                trailNodes.removeFirst();
            }
        } else {
            if (!trailNodes.isEmpty()) {
                trailNodes.removeFirst();
            }
        }
    }

    public void tick() {
        maxLife--;
    }

    public static class TrailNode {
        public Vec3 position;

        public TrailNode(Vec3 position) {
            this.position = position;
        }
    }

    public void render(float partialTick, PoseStack poseStack, Camera camera) {
        this.updatePos(this.getPosition(partialTick));
        if (trailNodes.size() <= 2) {
            return;
        }
        poseStack.pushPose();

        PoseStack.Pose last = poseStack.last();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        Vec3 camPos = camera.getPosition();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder begin = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        int a = rgbaColor >> 24;
        int r = (rgbaColor >> 16) & 0xFF;
        int g = (rgbaColor >> 8) & 0xFF;
        int b = rgbaColor & 0xFF;

        float halfThick = thickness * 0.5F;
        int size = trailNodes.size();

        for (int i = 0; i < size; i++) {
            Vec3 curr = trailNodes.get(i).position;
            Vec3 prev = i > 0 ? trailNodes.get(i - 1).position : curr;
            Vec3 dir = i > 0 ? curr.subtract(prev) : new Vec3(0, 0, 1);

            Vec3 thickDir = dir.scale(1.0 / dir.length()).cross(new Vec3(0, 1, 0)).cross(dir.scale(1.0 / dir.length())).normalize();

            float thick = (i / (float) (size - 1)) < fadeStartRatio ? halfThick * ((float) i / (size - 1) / fadeStartRatio) : halfThick;

            Vec3 o1 = thickDir.scale(thick);
            Vec3 o2 = thickDir.scale(-thick);

            begin.addVertex(last, (float) (curr.x + o1.x), (float) (curr.y + o1.y), (float) (curr.z + o1.z)).setColor(r, g, b, a);
            begin.addVertex(last, (float) (curr.x + o2.x), (float) (curr.y + o2.y), (float) (curr.z + o2.z)).setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(begin.build());
        RenderSystem.enableCull();
        poseStack.popPose();
    }
}
