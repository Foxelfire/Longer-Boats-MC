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
import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.screen.LongBoatScreen;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.foxelfire.longer_boats.screen.ModScreenHandlers;
import net.foxelfire.longer_boats.util.InventorySyncS2CPayload;
import net.foxelfire.longer_boats.util.MovementInputS2CPayload;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public class LongerBoatsModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.LONG_BOAT, LongBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_BOAT, LongBoatModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.LONG_RAFT, LongRaftRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_RAFT, LongRaftModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.LONG_BOAT_SCREEN_HANDLER, LongBoatScreen::new);
        
        ClientPlayNetworking.registerGlobalReceiver(InventorySyncS2CPayload.ID, (payload, context) -> {
            ArrayList<ItemStack> inventory = payload.inventory();
            int entityId = payload.entityId();
            boolean inScreen = payload.inScreen();
            int nextTab = payload.nextTab();

            context.client().execute(() -> {
                AbstractLongBoatEntity entity = (AbstractLongBoatEntity)context.player().getWorld().getEntityById(entityId);
                if(entity != null){
                    for(int i = 0; i < inventory.size(); i++){
                        entity.setInventoryStack(i, inventory.get(i));
                    }
                    if(inScreen){
                        int tabToManage = Math.max(nextTab, 0);
                        LongBoatScreenHandler.manageActiveEntityInventory(tabToManage);
                    }
                }
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
