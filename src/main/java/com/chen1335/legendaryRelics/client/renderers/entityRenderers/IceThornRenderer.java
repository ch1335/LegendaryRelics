package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.misc.IceThorn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * 冰刺渲染：两片互相垂直的竖直四边形（照搬 {@link IcicleRenderer} 的写顶点方式，但尖朝上）。
 * <p>贴图 32x32 里冰刺占 x=9..21、y=3..31，尖端在上；渲染时尖端映射到四边形顶部。
 */
public class IceThornRenderer extends EntityRenderer<IceThorn> {

    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/entity/ice_thorn.png");

    private static final float U0 = 9.0F / 32.0F;
    private static final float U1 = 22.0F / 32.0F;
    /** 贴图顶部是尖，底部是宽端。 */
    private static final float V_TIP = 3.0F / 32.0F;
    private static final float V_BASE = 32.0F / 32.0F;

    private static final float HALF_WIDTH = IceThorn.WIDTH / 2.0F;
    private static final float HEIGHT = IceThorn.HEIGHT;

    public IceThornRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(IceThorn entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 钻出动画：整片从地下升起，底部先露头，随后完全伸出
        float progress = Mth.clamp((entity.tickCount + partialTick) / IceThorn.EMERGE_TICKS, 0.0F, 1.0F);
        poseStack.translate(0.0F, -(1.0F - progress) * HEIGHT, 0.0F);

        // entityCutoutNoCull：不剔除背面，每片一个四边形就能双面可见
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        // 第 1 片：XY 平面
        vertex(pose, consumer, packedLight, -HALF_WIDTH, 0.0F, 0.0F, U0, V_BASE, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, HALF_WIDTH, 0.0F, 0.0F, U1, V_BASE, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, HALF_WIDTH, HEIGHT, 0.0F, U1, V_TIP, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, -HALF_WIDTH, HEIGHT, 0.0F, U0, V_TIP, 0.0F, 0.0F, 1.0F);

        // 第 2 片：ZY 平面，与第 1 片垂直
        vertex(pose, consumer, packedLight, 0.0F, 0.0F, -HALF_WIDTH, U0, V_BASE, 1.0F, 0.0F, 0.0F);
        vertex(pose, consumer, packedLight, 0.0F, 0.0F, HALF_WIDTH, U1, V_BASE, 1.0F, 0.0F, 0.0F);
        vertex(pose, consumer, packedLight, 0.0F, HEIGHT, HALF_WIDTH, U1, V_TIP, 1.0F, 0.0F, 0.0F);
        vertex(pose, consumer, packedLight, 0.0F, HEIGHT, -HALF_WIDTH, U0, V_TIP, 1.0F, 0.0F, 0.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight,
                               float x, float y, float z, float u, float v,
                               float normalX, float normalY, float normalZ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IceThorn entity) {
        return TEXTURE;
    }
}
