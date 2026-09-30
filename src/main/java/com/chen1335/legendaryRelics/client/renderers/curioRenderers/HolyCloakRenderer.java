package com.chen1335.legendaryRelics.client.renderers.curioRenderers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.module.curio.HolyCloakModel;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LREntityData;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 神圣斗篷的饰品渲染。
 *
 * <p>只绘制模型里的披风部件：纹理仅覆盖披风所在的 UV 区域，
 * 若整模型渲染，{@code HumanoidModel} 自带的身体/四肢部件会采样到同一片
 * 纹理而出现错位贴图。
 *
 * <p>飘扬的倾角照搬原版披风逻辑，状态存在 {@link LREntityData.CloakRenderData}。
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
        ModelPart cloak = model.getCloak();
        M model1 = renderLayerParent.getModel();
        if (model1 instanceof HumanoidModel<?> humanoidModel) {
            cloak.copyFrom(humanoidModel.body);
        }

        ICurioRenderer.followBodyRotations(living, model);

        // 飘扬状态由 ClientEventHandler 逐 tick 推进，渲染只读取
        LREntityData.CloakRenderData cloakData = LREntityData.getData(living).cloakRender;

        cloak.xRot = cloakData.getXRot(living, partialTicks)*0.75F;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);
    }
}
