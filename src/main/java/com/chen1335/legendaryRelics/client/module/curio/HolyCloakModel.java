package com.chen1335.legendaryRelics.client.module.curio;


import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class HolyCloakModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(LegendaryRelics.id("holy_cloak_layer"), "main");
    private final ModelPart cloak;

    public HolyCloakModel(ModelPart root) {
        super(root);
        this.cloak = root.getChild("cloak");
    }

    @Override
    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(cloak);
    }

    public static LayerDefinition createMesh(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cloak = partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, 0.0F, -2.0F, 16.0F, 23.0F, 4.0F, new CubeDeformation(1.4F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    /**
     * 披风部件。
     *
     * <p>纹理只绘制了披风本身，而 {@code HumanoidModel} 自带的身体/四肢部件会采样到同一片
     * UV 区域，因此渲染时必须只画这个部件（以及需要时的 {@link #head}）。
     */
    public ModelPart getCloak() {
        return this.cloak;
    }
}