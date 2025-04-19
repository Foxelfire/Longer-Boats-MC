package net.foxelfire.longer_boats.entity.client;

import net.foxelfire.longer_boats.entity.custom.LongRaftEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class LongRaftRenderer extends EntityRenderer<LongRaftEntity>{
    private LongRaftModel<LongRaftEntity> model;

    public LongRaftRenderer(Context ctx) {
        super(ctx);
        this.model = new LongRaftModel<LongRaftEntity>(ctx.getPart(ModModelLayers.LONG_RAFT));
        this.shadowRadius = 0.8f;
    }

    @Override
    public Identifier getTexture(LongRaftEntity entity) {
        return entity.getVariant().getTexture();
    }

    @Override
    public void render(LongRaftEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light){
        if(entity.getVariant() != null){
            matrixStack.push();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(this.getTexture(entity)));
            matrixStack.translate(0.0f, 1.5f, 0.0f);
            matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180.0f));
            float newYaw = (float)(entity.getYaw()*(Math.PI/180.0f));
            model.setAngles(entity, 0, 0, entity.age, newYaw, entity.getPitch());
            model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrixStack.pop();
            super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
        }
    }
}
