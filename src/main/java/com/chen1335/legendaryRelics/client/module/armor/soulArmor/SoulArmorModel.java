package com.chen1335.legendaryRelics.client.module.armor.soulArmor;// Made with Blockbench 5.2.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SoulArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LegendaryRelics.id("soul_armor_layer"), "main");

    public final ModelPart rightFoot;
    public final ModelPart leftFoot;
    public final ModelPart belt;

    public SoulArmorModel(ModelPart root) {
        super(root);
        this.rightFoot = root.getChild("right_foot");
        this.leftFoot = root.getChild("left_foot");
        this.belt = root.getChild("belt");
    }

    @Override
    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.rightFoot, this.leftFoot, this.hat, belt);
    }

    @Override
    public void copyPropertiesTo(HumanoidModel<T> model) {
        super.copyPropertiesTo(model);
        rightFoot.copyFrom(rightLeg);
        leftFoot.copyFrom(leftLeg);
        belt.copyFrom(body);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        rightFoot.visible = visible;
        leftFoot.visible = visible;
        belt.visible = visible;
    }

    public static LayerDefinition createMesh(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition right_leg_r1 = right_leg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-4.8962F, -11.9128F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.42F)).mirror(false), PartPose.offsetAndRotation(1.65F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        PartDefinition left_leg_r1 = left_leg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(0, 34).addBox(0.8962F, -11.9128F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.42F)), PartPose.offsetAndRotation(-1.65F, 12.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(-18, 0).addBox(-9.0F, -6.25F, -9.0F, 18.0F, 0.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(37, 0).addBox(-5.0F, -9.25F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(37, 0).mirror().addBox(1.9346F, -38.2529F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(68, 0).mirror().addBox(-0.0654F, -36.2529F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 25.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 18).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F)).mirror(false)
                .texOffs(41, 18).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F))
                .texOffs(41, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition right_foot = partdefinition.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(16, 34).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false), PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition left_foot = partdefinition.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(16, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(1.9F, 12.0F, 0.0F));

        PartDefinition belt = partdefinition.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(32, 34).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.44F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}