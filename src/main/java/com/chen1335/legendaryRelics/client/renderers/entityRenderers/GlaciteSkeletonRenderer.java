package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.module.entity.GlaciteSkeletonModel;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.GlaciateSkeleton;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;

/** 复用原版骷髅模型（含盔甲层），只替换贴图与手臂姿态。 */
public class GlaciteSkeletonRenderer extends SkeletonRenderer<GlaciateSkeleton> {

    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/entity/glacite_skeleton.png");

    public GlaciteSkeletonRenderer(EntityRendererProvider.Context context) {
        // 主模型换成自定义的，盔甲层沿用原版骷髅
        super(context, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR,
                new GlaciteSkeletonModel(context.bakeLayer(ModelLayers.SKELETON)));
    }

    @Override
    public ResourceLocation getTextureLocation(GlaciateSkeleton entity) {
        return TEXTURE;
    }
}
