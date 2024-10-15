package net.foxelfire.tutorialmod.entity.client;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

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

    @Override
    public void render(CedarBoatEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light){
        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrixStack, vertexConsumer, light, light, light, yaw, tickDelta, light);
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

}
