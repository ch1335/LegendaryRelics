package com.chen1335.legendaryRelics.client.clientExtensions;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.client.LRArmPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class ShadowDaggerExtension implements IClientItemExtensions {
    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
        if (player.getUseItem() != stack) {
            return false;
        }
        float maxChargeTick = stack.getItem() instanceof IChargeAbleItem chargeAbleItem ? chargeAbleItem.getMaxChargeTick(stack, player) : 10;
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float) i * 0.86F, -0.52F + equipProcess * -0.6F, -0.72F);
        boolean flag3 = arm == HumanoidArm.RIGHT;
        int k = flag3 ? 1 : -1;

        poseStack.translate((float) k * -0.5F, 0.7F, 0.1F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-55.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) k * 35.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) k * -9.785F));
        float f7 = (float) stack.getUseDuration(player) - ((float) player.getUseItemRemainingTicks() - partialTick + 1.0F);
        float f11 = f7 / maxChargeTick;
        if (f11 > 1.0F) {
            f11 = 1.0F;
        }

        if (f11 > 0.1F) {
            float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            poseStack.translate(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }

        poseStack.translate(0.0F, 0.0F, f11 * 0.2F);
        poseStack.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) k * 45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-65.0F));
        return true;
    }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return LRArmPose.DAGGER_CHARGE.getValue();
    }
}
