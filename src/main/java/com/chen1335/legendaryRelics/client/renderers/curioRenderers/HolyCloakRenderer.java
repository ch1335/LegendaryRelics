package com.chen1335.legendaryRelics.client.renderers.curioRenderers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.module.curio.HolyCloakModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 神圣斗篷的饰品渲染。
 *
 * <p>只绘制模型里的披风部件：纹理仅覆盖披风所在的 UV 区域，
 * 若整模型渲染，{@code HumanoidModel} 自带的身体/四肢部件会采样到同一片
 * 纹理而出现错位贴图。
 */
public class HolyCloakRenderer implements ICurioRenderer {

    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/curio/holy_cloak.png");

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity living = slotContext.entity();
        HolyCloakModel<LivingEntity> model = EntityRendererRegister.HOLY_CLOAK_MODEL;
        if (model == null) {
            return;
        }
        M model1 = renderLayerParent.getModel();
        ModelPart cloak = model.getCloak();
        cloak.xRot = 0;
        if (model1 instanceof HumanoidModel<?> humanoidModel) {
            cloak.copyFrom(humanoidModel.body);
        }


        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);
    }
}
