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
import net.foxelfire.longer_boats.util.ModNetworkingConstants;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class LongerBoatsModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.LONG_BOAT, LongBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_BOAT, LongBoatModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.LONG_RAFT, LongRaftRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LONG_RAFT, LongRaftModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.LONG_BOAT_SCREEN_HANDLER, LongBoatScreen::new);
        
        ClientPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.INVENTORY_S2C_SYNCING_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                byte invSize = buf.readByte();
                DefaultedList<ItemStack> invContents = DefaultedList.of();
                for(int i = 0; i < invSize; i++){
                    invContents.add(i, buf.readItemStack()); // scary!
                }
                int entityID = buf.readInt();
                boolean inScreen = buf.readBoolean();
                AbstractLongBoatEntity entity = (AbstractLongBoatEntity)handler.getWorld().getEntityById(entityID);
                if(entity != null){
                    entity.setInventory(invContents);
                }
                if(inScreen){
                    int nextTab = buf.readInt();
                    int tabToManage = nextTab == -1 ? 0 : nextTab;
                    LongBoatScreenHandler.manageActiveEntityInventory(tabToManage);
                }
            });
        });
    ClientPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.TOTAL_MOVEMENT_INPUTS_S2C_PACKET_ID, (client, handler, buf, responseSender) -> {
            int playerID = buf.readInt();
            float forwardSpeed = buf.readFloat();
            float sidewaysSpeed = buf.readFloat();
            if(handler != null){
                PlayerEntity otherPlayer = (PlayerEntity)handler.getWorld().getEntityById(playerID);
                if(otherPlayer != null){
                    otherPlayer.forwardSpeed = forwardSpeed;
                    otherPlayer.sidewaysSpeed = sidewaysSpeed;
                }
            }
        });
    }
}
