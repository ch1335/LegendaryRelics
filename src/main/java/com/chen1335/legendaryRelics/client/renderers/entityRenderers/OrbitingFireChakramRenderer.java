package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.OrbitingFireChakram;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * 环绕飞轮渲染器：直接渲染烈焰飞轮的物品模型并叠加自转；
 * 位置同步由服务端负责，这里只补客户端视觉轨迹的差值。
 */
public class OrbitingFireChakramRenderer extends EntityRenderer<OrbitingFireChakram> {

    private final ItemRenderer itemRenderer;
    private final ItemStack renderStack;

    public OrbitingFireChakramRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.renderStack = LRItems.BLAZING_CHAKRAM.get().getDefaultInstance();
    }

    @Override
    public void render(OrbitingFireChakram entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // EntityRenderer 已把 PoseStack 移到网络插值位置；补上客户端独立视觉轨迹与该位置的差值
        Vec3 syncedPosition = entity.getPosition(partialTick);
        Vec3 visualPosition = entity.getClientVisualPosition(partialTick);
        Vec3 offset = visualPosition.subtract(syncedPosition);
        poseStack.translate(offset.x, offset.y, offset.z);

        poseStack.translate(0.0D, 0.3D, 0.0D);
        // 自转是纯视觉，直接用 tickCount + partialTick 算，不需要同步
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTick) * 45.0F));

        this.itemRenderer.renderStatic(this.renderStack, ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull OrbitingFireChakram entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
