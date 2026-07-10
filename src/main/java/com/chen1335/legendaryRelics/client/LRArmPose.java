package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class LRArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> DAGGER_CHARGE = new EnumProxy<>(
            HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) (model, entity, arm) -> {
        Player clientPlayer = LRClient.getClientPlayer();
        if (clientPlayer.isUsingItem()) {
            ItemStack useItem = clientPlayer.getUseItem();
            float maxChargeTick = useItem.getItem() instanceof IChargeAbleItem chargeAbleItem ? chargeAbleItem.getMaxChargeTick(useItem, clientPlayer) : 10;
            float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            float f7 = (float) useItem.getUseDuration(clientPlayer) - ((float) clientPlayer.getUseItemRemainingTicks() - partialTick + 1.0F);
            float f11 = f7 / maxChargeTick;
            if (f11 > 1.0F) {
                f11 = 1.0F;
            }
            if (model.leftArmPose == LRArmPose.DAGGER_CHARGE.getValue()) {
                model.leftArm.xRot = -Mth.HALF_PI - f11 * Mth.HALF_PI;
                model.leftArm.yRot = 0.0F;
            } else if (model.rightArmPose == LRArmPose.DAGGER_CHARGE.getValue()) {
                model.rightArm.xRot = -Mth.HALF_PI - f11 * Mth.HALF_PI;
                model.rightArm.yRot = 0.0F;
            }
        }
    }
    );
}
