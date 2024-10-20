package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class CedarBoatRenderer extends EntityRenderer<CedarBoatEntity>{
    private static final Identifier TEXTURE = new Identifier(TutorialMod.MOD_ID, "textures/entity/cedar_boat.png");
    private CedarBoatModel<CedarBoatEntity> model;

    public CedarBoatRenderer(Context ctx) {
        super(ctx);
        this.model = new CedarBoatModel<CedarBoatEntity>(ctx.getPart(ModModelLayers.CEDAR_BOAT));
        this.shadowRadius = 0.8f;
    }

    @Override
    public Identifier getTexture(CedarBoatEntity var1) {
        return TEXTURE;
    }
    // need to fix something? check out BoatEntityRenderer and maybe that whole type hierarchy
    @Override
    public void render(CedarBoatEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light){
        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        matrixStack.translate(0.0f, 1.44f, 0.0f);
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180.0f));
        model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        model.setAngles(entity, 0, 0, entity.age, yaw, entity.getPitch());
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

}
