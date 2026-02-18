package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.dataComponentTypes.BowUsingArrow;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ArrowInBowRender {
    public static final Map<Item, AbstractArrow> ABSTRACT_ARROW_MAP = new HashMap<>();

    public static void onRenderItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel pModel) {
        if (itemStack.is(LRItems.LAST_WHISPER)) {
            if (0 < displayContext.getId() && displayContext.getId() < 5) {
                BowUsingArrow bowUsingArrow = itemStack.get(LRDataComponentTypes.BOW_USING_ARROW);
                if (bowUsingArrow != null) {
                    if (bowUsingArrow.itemStack().getItem() instanceof ArrowItem arrowItem) {
                        AbstractArrow abstractArrow = ABSTRACT_ARROW_MAP.computeIfAbsent(arrowItem, item -> arrowItem.createArrow(Minecraft.getInstance().level, bowUsingArrow.itemStack(), Minecraft.getInstance().player, null));
                        poseStack.pushPose();
                        transform(itemStack, displayContext, poseStack);
                        EntityRenderer<? super AbstractArrow> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(abstractArrow);
                        renderer.render(abstractArrow, 0, 0, poseStack, bufferSource, combinedLight);
                        poseStack.popPose();
                    }
                }
            }
        }
    }

    private static void transform(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack) {
        float z;
        float pull = ItemProperties.getProperty(itemStack, ResourceLocation.withDefaultNamespace("pull")).call(itemStack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
        if (pull >= 0.9) {
            z = 0.1F;
        } else if (pull >= 0.65) {
            z = 0.3F;
        } else {
            z = 0.4F;
        }
        if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.translate(0.04, 0.22, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.XP.rotationDegrees(-20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            poseStack.translate(0, 0, z);
        } else if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            poseStack.translate(-0.04, 0.22, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.XP.rotationDegrees(-20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            poseStack.translate(0, 0, z);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.translate(-0.1, -0.09, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZP.rotationDegrees(35));
            poseStack.translate(0, 0, z);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.translate(0.1, -0.09, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZP.rotationDegrees(35));
            poseStack.translate(0, 0, z);
        }
    }
}
