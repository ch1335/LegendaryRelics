package com.chen1335.legendaryRelics.client.module.entity;

import com.chen1335.legendaryRelics.client.LRArmPose;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.GlaciteSkeleton;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons.FrostShard;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

/** 蓄力时举起霜锥，复用玩家那套蓄力姿态。 */
public class GlaciteSkeletonModel extends SkeletonModel<GlaciteSkeleton> {

    public GlaciteSkeletonModel(ModelPart root) {
        super(root);
    }

    @Override
    public void prepareMobModel(GlaciteSkeleton entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        if (!entity.isUsingItem() || !(entity.getUseItem().getItem() instanceof FrostShard)) {
            return;
        }
        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = LRArmPose.TRIDENT_CHARGE.getValue();
        } else {
            this.leftArmPose = LRArmPose.TRIDENT_CHARGE.getValue();
        }
    }
}
