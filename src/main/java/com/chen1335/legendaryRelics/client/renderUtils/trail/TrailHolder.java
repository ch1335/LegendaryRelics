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

public class TrailHolder {
    private final Supplier<Boolean> needRemove;
    private final Function<Float, Vec3> position;
    private final int maxLength;
    private final int j;
    public List<TrailNode> trailNodes = new ArrayList<>();

    public int maxLife = 400;

    public boolean needRemove() {
        return trailNodes.isEmpty() || maxLife < 0;
    }

    public Vec3 getPosition(float partialTick) {
        return position.apply(partialTick);
    }

    public TrailHolder(Supplier<Boolean> needRemove, Function<Float, Vec3> position,int maxLength,int j) {
        this.needRemove = needRemove;
        this.position = position;
        this.maxLength = maxLength;
        this.j = j;
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
        if (trailNodes.size() <= 1) {
            return;
        }
        poseStack.pushPose();

        PoseStack.Pose last = poseStack.last();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        Vec3 position = camera.getPosition();
        poseStack.translate(-position.x, -position.y, -position.z);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder begin = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < trailNodes.size(); i++) {
            float h = 0.1F;
            float x = 0F;
            float z = 0F;
            if (i <= j) {
                h = 0 + h / j * i;
            }
            TrailNode trailNode = trailNodes.get(i);

            Vec3 positionFrom = trailNode.position;

            begin.addVertex(last, positionFrom.toVector3f().add(x, h, z)).setColor(255, 255, 255, 100);
            begin.addVertex(last, positionFrom.toVector3f().add(-x, -h, -z)).setColor(255, 255, 255, 100);
        }

        MeshData meshdata = begin.build();
        if (meshdata != null) {
            BufferUploader.drawWithShader(meshdata);
        }

        RenderSystem.enableCull();
        poseStack.popPose();
    }
}
