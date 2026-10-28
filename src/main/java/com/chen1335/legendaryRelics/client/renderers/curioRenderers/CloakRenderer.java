package com.chen1335.legendaryRelics.client.renderers.curioRenderers;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LREntityData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * 通用披风渲染器：模型走盔甲骨架，{@code cloak} 骨骼跟随躯干并叠加 {@link LREntityData.CloakRenderData} 的飘动。
 *
 * <p>约定骨骼名：{@code cloak}（披风）、{@code armorBody}/{@code bipedHead}/{@code armorHead}（盔甲骨架）。
 */
public class CloakRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> implements ICurioRenderer {

    private static final String CLOAK_BONE = "cloak";
    private static final String BODY_BONE = "armorBody";
    private static final String HEAD_PARENT_BONE = "bipedHead";

    private float flapRot;

    // 骨骼缓存：资源重载会重新烘焙模型，换模型实例时重取
    @Nullable
    private BakedGeoModel cachedModel;
    @Nullable
    private GeoBone bodyBone;
    @Nullable
    private GeoBone cloakBone;
    @Nullable
    private GeoBone headParentBone;

    public CloakRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public <E extends LivingEntity, M extends EntityModel<E>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<E, M> renderLayerParent, MultiBufferSource buffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity living = slotContext.entity();
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        // 飘扬状态由 ClientEventHandler 逐 tick 推进，渲染只读取
        LREntityData.CloakRenderData cloakData = LREntityData.getData(living).cloakRender;
        this.flapRot = cloakData.getXRot(living, partialTicks) * 0.75F;
        prepForRender(living, stack, EquipmentSlot.CHEST, humanoidModel, buffer, partialTicks,
                limbSwing, limbSwingAmount, netHeadYaw, headPitch);
        RenderType renderType = getRenderType(getAnimatable(), getTextureLocation(getAnimatable()), buffer, partialTicks);
        defaultRender(poseStack, getAnimatable(), buffer, renderType, buffer.getBuffer(renderType),
                0F, partialTicks, light);
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllBonesVisible(false);
        setBoneVisible(this.headParentBone, true);
        setBoneVisible(this.head, true);
        setBoneVisible(this.cloakBone, true);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, int colour) {
        cacheBones(model);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        GeoBone body = this.bodyBone;
        GeoBone cloak = this.cloakBone;
        if (body != null && cloak != null) {
            cloak.updatePosition(body.getPosX(), body.getPosY(), body.getPosZ());
            // 盔甲渲染空间对 X/Y 轴做了翻转，旋转符号与 HumanoidModel 相反
            cloak.setRotX(-this.flapRot);
            cloak.setRotY(-body.getRotY());
            cloak.setRotZ(body.getRotZ());
        }
    }

    private void cacheBones(BakedGeoModel model) {
        if (model == this.cachedModel) {
            return;
        }
        this.cachedModel = model;
        this.bodyBone = model.getBone(BODY_BONE).orElse(null);
        this.cloakBone = model.getBone(CLOAK_BONE).orElse(null);
        this.headParentBone = model.getBone(HEAD_PARENT_BONE).orElse(null);
    }
}
