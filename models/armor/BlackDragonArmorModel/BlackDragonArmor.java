// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class BlackDragonArmor<T extends Humanoid> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "blackdragonarmor"), "main");
	private final ModelPart hat;
	private final ModelPart head;
	private final ModelPart horn_left;
	private final ModelPart hron_right;
	private final ModelPart body;
	private final ModelPart belt;
	private final ModelPart right_arm;
	private final ModelPart shoulder_armor;
	private final ModelPart right_plate;
	private final ModelPart left_arm;
	private final ModelPart shoulder_armor2;
	private final ModelPart left_plate;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart right_foot;
	private final ModelPart left_foot;

	public BlackDragonArmor(ModelPart root) {
		this.hat = root.getChild("hat");
		this.head = root.getChild("head");
		this.horn_left = this.head.getChild("horn_left");
		this.hron_right = this.head.getChild("hron_right");
		this.body = root.getChild("body");
		this.belt = root.getChild("belt");
		this.right_arm = root.getChild("right_arm");
		this.shoulder_armor = this.right_arm.getChild("shoulder_armor");
		this.right_plate = this.shoulder_armor.getChild("right_plate");
		this.left_arm = root.getChild("left_arm");
		this.shoulder_armor2 = this.left_arm.getChild("shoulder_armor2");
		this.left_plate = this.shoulder_armor2.getChild("left_plate");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
		this.right_foot = root.getChild("right_foot");
		this.left_foot = root.getChild("left_foot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
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

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.6F))
		.texOffs(118, 1).addBox(-2.0F, 1.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 4).addBox(-4.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 7).addBox(-3.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 7).mirror().addBox(2.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(124, 4).mirror().addBox(3.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition belt = partdefinition.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(104, 13).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(3.0F, 12.0F, -1.0F));

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

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 32).addBox(-3.9F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-0.1F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_foot = partdefinition.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(48, 0).addBox(-4.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition left_foot = partdefinition.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(48, 7).addBox(0.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Humanoid entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		belt.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}