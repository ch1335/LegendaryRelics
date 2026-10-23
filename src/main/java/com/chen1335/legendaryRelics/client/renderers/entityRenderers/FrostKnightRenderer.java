package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.client.module.entity.FrostKnightModel;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.FrostKnight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

/**
 * hand_item 骨骼就是为主手物品准备的：渲染时会先套用该骨骼当前的动画变换，
 * 所以 attack_1 里 hand_item 的关键帧会直接带动武器。
 */
public class FrostKnightRenderer extends GeoEntityRenderer<FrostKnight> {

    private static final String HAND_ITEM_BONE = "item";

    public FrostKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostKnightModel());
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this,
                // 每帧每根骨骼都会调到，先比名字再返回
                (bone, animatable) -> HAND_ITEM_BONE.equals(bone.getName()) ? animatable.getMainHandItem() : null,
                (bone, animatable) -> null) {
            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, FrostKnight animatable) {
                // 默认是 NONE，物品会以原始姿态贴在骨骼原点上
                return ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            }
        });
    }
}
