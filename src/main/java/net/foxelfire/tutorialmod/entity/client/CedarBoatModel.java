package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.TutorialMod;

// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("unused") // thanks again, blockbench!
public class CedarBoatModel<T extends CedarBoatEntity> extends SinglePartEntityModel<T> {
	private final ModelPart boat;
	private final ModelPart front;
	private final ModelPart paddles;
	private final ModelPart base;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart back;
	private final ModelPart bottom_no_water;
	private final ModelPart paddle_right;
	private final ModelPart paddle_left;
	private final ModelPart paddle_back_right;
	private final ModelPart paddle_back_left;
	public CedarBoatModel(ModelPart root) {
		this.boat = root.getChild("boat");
		this.front = this.boat.getChild("front");
		this.base = this.front.getChild("base");
		this.left = this.boat.getChild("left");
		this.right = this.boat.getChild("right");
		this.back = this.boat.getChild("back");
		this.bottom_no_water = this.boat.getChild("bottom_no_water");
		this.paddles = this.boat.getChild("paddles");
		this.paddle_right = this.paddles.getChild("paddle_right");
		this.paddle_left = this.paddles.getChild("paddle_left");
		this.paddle_back_right = this.paddles.getChild("paddle_back_right");
		this.paddle_back_left = this.paddles.getChild("paddle_back_left");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData boat = modelPartData.addChild("boat", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData front = boat.addChild("front", ModelPartBuilder.create().uv(0, 19).cuboid(-9.0F, -7.0F, 28.0F, 18.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 5.5F, 0.0F, 3.1416F, 0.0F));

		ModelPartData base = front.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-23.5F, -8.0F, -1.0F, 56.0F, 16.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 4.5F, 1.5708F, 1.5708F, 0.0F));

		ModelPartData left = boat.addChild("left", ModelPartBuilder.create().uv(0, 35).cuboid(-24.5F, -3.0F, -0.5F, 56.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(9.5F, -6.0F, 2.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData right = boat.addChild("right", ModelPartBuilder.create().uv(0, 43).cuboid(-32.5F, -3.0F, -18.5F, 56.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(8.5F, -6.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData back = boat.addChild("back", ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -3.0F, -19.0F, 16.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, 16.5F, 0.0F, 3.1416F, 0.0F));

		ModelPartData bottom_no_water = boat.addChild("bottom_no_water", ModelPartBuilder.create().uv(188, 41).cuboid(-14.0F, -9.0F, -6.0F, 28.0F, 17.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 11.0F, 3.5F));

		ModelPartData paddles = boat.addChild("paddles", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData paddle_right = paddles.addChild("paddle_right", ModelPartBuilder.create().uv(118, 0).cuboid(2.0F, -0.5F, -5.75F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(18, 51).cuboid(3.01F, -3.5F, 7.25F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(158, 1).cuboid(0.0F, -1.0F, -3.0F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(9.5F, -5.5F, -13.25F));

		ModelPartData paddle_left = paddles.addChild("paddle_left", ModelPartBuilder.create().uv(118, 0).cuboid(-4.0F, -0.5F, -5.75F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(0, 51).cuboid(-4.01F, -3.5F, 7.25F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-9.5F, -5.5F, -13.25F));

		ModelPartData front_left_hold_r1 = paddle_left.addChild("front_left_hold_r1", ModelPartBuilder.create().uv(157, 1).cuboid(-4.0F, -2.0F, -1.0F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 1.0F, -1.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData paddle_back_right = paddles.addChild("paddle_back_right", ModelPartBuilder.create().uv(118, 0).cuboid(2.0F, -0.5F, -5.75F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(18, 51).cuboid(3.01F, -3.5F, 6.25F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(158, 1).cuboid(0.0F, -1.0F, -3.0F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(9.5F, -5.5F, 14.75F));

		ModelPartData paddle_back_left = paddles.addChild("paddle_back_left", ModelPartBuilder.create().uv(118, 0).cuboid(-4.0F, -0.5F, -5.75F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(0, 51).cuboid(-4.01F, -3.5F, 6.25F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(158, 1).cuboid(-5.0F, -1.0F, -3.0F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-9.5F, -5.5F, 14.75F));
		return TexturedModelData.of(modelData, 256, 64);
	}
	@Override
	public void setAngles(CedarBoatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		boat.yaw = netHeadYaw;
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		boat.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return boat;
	}
}