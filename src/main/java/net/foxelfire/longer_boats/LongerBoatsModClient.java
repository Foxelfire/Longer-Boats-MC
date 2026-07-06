package net.foxelfire.longer_boats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.foxelfire.longer_boats.entity.ModEntities;
import net.foxelfire.longer_boats.entity.client.LongBoatModel;
import net.foxelfire.longer_boats.entity.client.LongBoatRenderer;
import net.foxelfire.longer_boats.entity.client.LongRaftModel;
import net.foxelfire.longer_boats.entity.client.LongRaftRenderer;
import net.foxelfire.longer_boats.entity.client.ModModelLayers;
import net.foxelfire.longer_boats.screen.LongBoatScreen;
import net.foxelfire.longer_boats.screen.ModScreenHandlers;
import net.foxelfire.longer_boats.util.InventorySizeS2CPayload;
import net.foxelfire.longer_boats.util.MovementInputS2CPayload;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;

public class LongerBoatsModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.LONG_BOAT, LongBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_BOAT, LongBoatModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.LONG_RAFT, LongRaftRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_RAFT, LongRaftModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.LONG_BOAT_SCREEN_HANDLER, LongBoatScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(InventorySizeS2CPayload.ID, (payload, context) -> {
            int entityId = payload.entityId();
            int chestCount = payload.chestCount();
            context.client().execute(() -> {

            });
        });

        ClientPlayNetworking.registerGlobalReceiver(MovementInputS2CPayload.ID, (payload, context) -> {
            int playerId = payload.entityId();
            float forwardSpeed = payload.forwardSpeed();
            float sidewaysSpeed = payload.sidewaysSpeed();

            context.client().execute(() -> {
                if(context.player().getWorld().getEntityById(playerId) != null){
                    PlayerEntity otherPlayer = (PlayerEntity)context.player().getWorld().getEntityById(playerId);
                    if(otherPlayer != null){
                        otherPlayer.forwardSpeed = forwardSpeed;
                        otherPlayer.sidewaysSpeed = sidewaysSpeed;
                    }
                }
            });
        });
    }
}
