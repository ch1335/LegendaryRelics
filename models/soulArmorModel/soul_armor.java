// Made with Blockbench 5.2.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class soul_armor<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "soul_armor"), "main");
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_feet;
	private final ModelPart left_feet;

	public soul_armor(ModelPart root) {
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.right_arm = root.getChild("right_arm");
		this.left_arm = root.getChild("left_arm");
		this.right_feet = root.getChild("right_feet");
		this.left_feet = root.getChild("left_feet");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition right_leg_r1 = right_leg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-4.8962F, -11.9128F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition left_leg_r1 = left_leg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(0, 34).addBox(0.8962F, -11.9128F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(-18, 0).addBox(-9.0F, -7.25F, -9.0F, 18.0F, 0.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(37, 0).addBox(-5.0F, -10.25F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(37, 0).mirror().addBox(1.9346F, -38.2529F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(68, 0).mirror().addBox(-0.0654F, -36.2529F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 18).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F)).mirror(false)
		.texOffs(41, 18).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)).mirror(false), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.7F))
		.texOffs(41, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_feet = partdefinition.addOrReplaceChild("right_feet", CubeListBuilder.create().texOffs(16, 34).mirror().addBox(-3.9F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition left_feet = partdefinition.addOrReplaceChild("left_feet", CubeListBuilder.create().texOffs(16, 34).addBox(-0.1F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_feet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_feet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}