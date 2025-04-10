package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.entity.animation.LongBoatAnimations;
import net.foxelfire.tutorialmod.entity.custom.LongRaftEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;


// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// @author Foxelfire
@SuppressWarnings("unused")
public class LongRaftModel<T extends LongRaftEntity> extends SinglePartEntityModel<T> {
	private final ModelPart boat;
	private final ModelPart body;
	private final ModelPart paddles;
	private final ModelPart paddle_left;
	private final ModelPart paddle_right;
	private final ModelPart chests;
	private final ModelPart seat_0_chest;
	private final ModelPart seat_1_chest;
	private final ModelPart seat_2_chest;
	private final ModelPart seat_3_chest;
	private final ModelPart back_paddles;
	private final ModelPart paddle_back_left;
	private final ModelPart paddle_back_right;
	public LongRaftModel(ModelPart root) {
		this.boat = root.getChild("boat");
		this.body = this.boat.getChild("body");
		this.paddles = this.boat.getChild("paddles");
		this.paddle_left = this.paddles.getChild("paddle_left");
		this.paddle_right = this.paddles.getChild("paddle_right");
		this.chests = this.boat.getChild("chests");
		this.seat_0_chest = this.chests.getChild("seat_0_chest");
		this.seat_1_chest = this.chests.getChild("seat_1_chest");
		this.seat_2_chest = this.chests.getChild("seat_2_chest");
		this.seat_3_chest = this.chests.getChild("seat_3_chest");
		this.back_paddles = this.boat.getChild("back_paddles");
		this.paddle_back_left = this.back_paddles.getChild("paddle_back_left");
		this.paddle_back_right = this.back_paddles.getChild("paddle_back_right");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData boat = modelPartData.addChild("boat", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData body = boat.addChild("body", ModelPartBuilder.create().uv(0, 64).cuboid(-10.0F, -11.0F, -32.0F, 20.0F, 4.0F, 60.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-8.0F, -7.0F, -32.0F, 16.0F, 4.0F, 60.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData paddles = boat.addChild("paddles", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData paddle_left = paddles.addChild("paddle_left", ModelPartBuilder.create().uv(0, 82).cuboid(-3.0F, -2.5F, -11.75F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(7, 106).cuboid(-3.01F, -5.5F, 1.25F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(27, 113).cuboid(-4.0F, -3.0F, -8.0F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-11.5F, -5.5F, -13.25F));

		ModelPartData paddle_right = paddles.addChild("paddle_right", ModelPartBuilder.create().uv(0, 82).cuboid(0.9967F, -0.6667F, -11.25F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(7, 106).cuboid(2.0067F, -3.6667F, 1.75F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(26, 113).cuboid(-1.0033F, -1.1667F, -7.5F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(12.5033F, -7.3333F, -13.75F));

		ModelPartData chests = boat.addChild("chests", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData seat_0_chest = chests.addChild("seat_0_chest", ModelPartBuilder.create().uv(112, 84).cuboid(-7.0F, -25.0F, -33.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F))
		.uv(162, 84).cuboid(-1.0F, -22.0F, -34.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData seat_1_chest = chests.addChild("seat_1_chest", ModelPartBuilder.create().uv(162, 84).cuboid(-1.0F, -22.0F, -18.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(112, 84).cuboid(-7.0F, -25.0F, -17.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData seat_2_chest = chests.addChild("seat_2_chest", ModelPartBuilder.create().uv(162, 84).cuboid(-1.0F, -22.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(112, 84).cuboid(-7.0F, -25.0F, -1.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData seat_3_chest = chests.addChild("seat_3_chest", ModelPartBuilder.create().uv(112, 84).cuboid(-7.0F, -25.0F, -1.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F))
		.uv(162, 84).cuboid(-1.0F, -22.0F, -2.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 16.0F));

		ModelPartData back_paddles = boat.addChild("back_paddles", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData paddle_back_left = back_paddles.addChild("paddle_back_left", ModelPartBuilder.create().uv(0, 82).cuboid(-2.9967F, -0.6667F, -11.25F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(7, 106).cuboid(-3.0067F, -3.6667F, 1.75F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(27, 113).cuboid(-3.9967F, -1.1667F, -7.5F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-11.5033F, -7.3333F, 14.25F));

		ModelPartData paddle_back_right = back_paddles.addChild("paddle_back_right", ModelPartBuilder.create().uv(0, 82).cuboid(0.9967F, -0.6667F, -10.25F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
		.uv(7, 106).cuboid(2.0067F, -3.6667F, 2.75F, 1.0F, 6.0F, 7.0F, new Dilation(0.0F))
		.uv(26, 113).cuboid(-1.0033F, -1.1667F, -6.5F, 5.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(11.5033F, -7.3333F, 13.25F));
		return TexturedModelData.of(modelData, 196, 128);
	}
	@Override
	public void setAngles(LongRaftEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		boat.yaw = netHeadYaw;
		this.updateAnimation(entity.frontRowingAnimationState, LongBoatAnimations.rowing_front, ageInTicks, 2f);
		this.updateAnimation(entity.rotatingLeftAnimationState, LongBoatAnimations.rotate_clockwise, ageInTicks, 1f);
		this.updateAnimation(entity.rotatingRightAnimationState, LongBoatAnimations.rotate_counterclockwise, ageInTicks, 1f);
		this.seat_0_chest.visible = entity.getChestPresent(0);
		this.seat_1_chest.visible = entity.getChestPresent(1);
		this.seat_2_chest.visible = entity.getChestPresent(2);
		this.seat_3_chest.visible = entity.getChestPresent(3);
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