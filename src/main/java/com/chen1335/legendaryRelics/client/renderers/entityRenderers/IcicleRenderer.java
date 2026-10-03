package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.Icicle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * 冰锥的渲染：两片互相垂直的四边形，做法照搬原版箭（直接写顶点，不用模型层）。
 *
 * <p>贴图里冰锥画在 32x32 画布的 x=10..22、y=0..26 区域，上宽下尖；
 * 映射时把「尖」放在 +X（飞行前方），所以 v 轴是反的。
 */
public class IcicleRenderer extends EntityRenderer<Icicle> {

    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/entity/icicle.png");

    /** 贴图中冰锥实际占用的 UV 范围。 */
    private static final float U0 = 10.0F / 32.0F;
    private static final float U1 = 22.0F / 32.0F;
    private static final float V0 = 0.0F;
    private static final float V1 = 26.0F / 32.0F;

    /** 四边形沿飞行方向的半长／垂直方向的半宽，比例与贴图区域一致，避免拉伸。 */
    private static final float HL = 8.0F;
    private static final float HW = 3.75F;

    /** 缩放，与原版箭一致（1 单位 ≈ 0.056 格）。 */
    private static final float SCALE = 0.05625F;

    public IcicleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Icicle entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 朝向与原版箭一致：Y 轴对齐飞行偏航、Z 轴对齐俯仰
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

        // 插在方块上之后的颤动
        float shake = (float) entity.shakeTime - partialTick;
        if (shake > 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.sin(shake * 3.0F) * shake));
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(SCALE, SCALE, SCALE);

        // entityCutoutNoCull：不剔除背面，所以每片只需一个四边形就能双面可见
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        // 第 1 片：位于 XY 平面。
        // 贴图里冰锥是竖直的（u 为宽度、v 为长度），所以 u 走四边形的宽度、v 走长度，
        // 并把 v=V1（贴图下端的尖端）放在 +X（飞行前方），v=V0（宽端）放在 -X。
        vertex(pose, consumer, packedLight, -HL, -HW, 0.0F, U0, V0, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, HL, -HW, 0.0F, U0, V1, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, HL, HW, 0.0F, U1, V1, 0.0F, 0.0F, 1.0F);
        vertex(pose, consumer, packedLight, -HL, HW, 0.0F, U1, V0, 0.0F, 0.0F, 1.0F);

        // 第 2 片：位于 XZ 平面，与第 1 片垂直
        vertex(pose, consumer, packedLight, -HL, 0.0F, -HW, U0, V0, 0.0F, 1.0F, 0.0F);
        vertex(pose, consumer, packedLight, HL, 0.0F, -HW, U0, V1, 0.0F, 1.0F, 0.0F);
        vertex(pose, consumer, packedLight, HL, 0.0F, HW, U1, V1, 0.0F, 1.0F, 0.0F);
        vertex(pose, consumer, packedLight, -HL, 0.0F, HW, U1, V0, 0.0F, 1.0F, 0.0F);

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
    public @NotNull ResourceLocation getTextureLocation(@NotNull Icicle entity) {
        return TEXTURE;
    }
}
