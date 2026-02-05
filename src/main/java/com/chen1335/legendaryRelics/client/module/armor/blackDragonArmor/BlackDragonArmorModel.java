package com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor;


import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BlackDragonArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LegendaryRelics.id("black_dragon_armor_layer"), "main");

    public final ModelPart leftFoot;
    public final ModelPart rightFoot;
    public final ModelPart belt;

    public BlackDragonArmorModel(ModelPart root) {
        super(root);
        this.rightFoot = root.getChild("right_foot");
        this.leftFoot = root.getChild("left_foot");
        this.belt = root.getChild("belt");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition horn_left = head.addOrReplaceChild("horn_left", CubeListBuilder.create().texOffs(46, 61).addBox(3.0F, -9.0F, -2.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 62).addBox(4.0F, -10.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F))
                .texOffs(64, 5).addBox(5.0F, -11.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F))
                .texOffs(58, 14).addBox(5.5F, -11.5F, 1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hron_right = head.addOrReplaceChild("hron_right", CubeListBuilder.create().texOffs(64, 45).addBox(-7.0F, -11.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F))
                .texOffs(64, 40).addBox(-6.0F, -10.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F))
                .texOffs(64, 0).addBox(-5.0F, -9.0F, -2.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 55).addBox(-7.5F, -11.5F, 1.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.7F))
                .texOffs(118, 1).addBox(-2.0F, 1.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(124, 4).addBox(-4.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(124, 7).addBox(-3.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(124, 7).mirror().addBox(2.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(124, 4).mirror().addBox(3.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition belt = partdefinition.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(104, 13).addBox(-4.0F, 8.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(0F, 0F, 0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition shoulder_armor = right_arm.addOrReplaceChild("shoulder_armor", CubeListBuilder.create().texOffs(50, 30).addBox(-4.0F, -3.0F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(58, 20).addBox(-4.0F, -2.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 60).addBox(-4.0F, -3.0F, -3.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 54).addBox(-4.0F, -3.0F, 2.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 40).addBox(-4.0F, 2.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(50, 47).addBox(-3.0F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 47).addBox(-3.0F, 1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 54).addBox(-4.0F, 1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_plate = shoulder_armor.addOrReplaceChild("right_plate", CubeListBuilder.create().texOffs(32, 32).addBox(0.5F, -1.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(32, 39).addBox(-1.5F, -0.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(40, 16).addBox(-3.5F, 0.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.4623F, -2.2137F, -0.5F, 0.0F, 0.0F, -0.5236F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition shoulder_armor2 = left_arm.addOrReplaceChild("shoulder_armor2", CubeListBuilder.create().texOffs(50, 35).addBox(-1.0F, -3.0F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(26, 60).addBox(3.0F, -2.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(60, 59).addBox(-1.0F, -3.0F, -3.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 61).addBox(-1.0F, -3.0F, 2.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 48).addBox(3.0F, 2.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 53).addBox(3.0F, 1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(5, 60).addBox(2.0F, 1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 47).mirror().addBox(2.0F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_plate = shoulder_armor2.addOrReplaceChild("left_plate", CubeListBuilder.create().texOffs(40, 23).addBox(-3.5F, -1.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(32, 46).addBox(-1.5F, -0.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F))
                .texOffs(0, 48).addBox(0.5F, 0.5F, -2.5F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.4623F, -2.2137F, -0.5F, 0.0F, 0.0F, 0.5236F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 32).addBox(-2F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-2F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

        PartDefinition right_foot = partdefinition.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(48, 0).addBox(-2F, 9F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)), PartPose.offset(-1.9F, 3.0F, 0.0F));

        PartDefinition left_foot = partdefinition.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(48, 7).addBox(-2F, 9F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)), PartPose.offset(1.9F, 3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat, this.rightFoot, this.leftFoot, this.belt);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        this.rightFoot.visible = visible;
        this.leftFoot.visible = visible;
        this.belt.visible = visible;
    }
}