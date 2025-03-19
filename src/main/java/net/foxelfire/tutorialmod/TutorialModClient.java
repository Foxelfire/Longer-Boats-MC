package net.foxelfire.tutorialmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.entity.client.CedarBoatModel;
import net.foxelfire.tutorialmod.entity.client.CedarBoatRenderer;
import net.foxelfire.tutorialmod.entity.client.ModModelLayers;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.foxelfire.tutorialmod.screen.CedarBoatScreen;
import net.foxelfire.tutorialmod.screen.CedarBoatScreenHandler;
import net.foxelfire.tutorialmod.screen.ModScreenHandlers;
import net.foxelfire.tutorialmod.util.ModNetworkingConstants;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class TutorialModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_SAPLING, RenderLayer.getCutout());
        EntityRendererRegistry.register(ModEntities.CEDAR_BOAT, CedarBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CEDAR_BOAT, CedarBoatModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, CedarBoatScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.INVENTORY_S2C_SYNCING_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                byte invSize = buf.readByte();
                DefaultedList<ItemStack> invContents = DefaultedList.of();
                for(int i = 0; i < invSize; i++){
                    invContents.add(i, buf.readItemStack()); // scary!
                }
                int entityID = buf.readInt();
                boolean inScreen = buf.readBoolean();
                CedarBoatEntity entity = (CedarBoatEntity)handler.getWorld().getEntityById(entityID);
                entity.setInventory(invContents);
                if(inScreen){
                    int nextTab = buf.readInt();
                    int tabToManage = nextTab == -1 ? 0 : nextTab;
                    CedarBoatScreenHandler.manageActiveEntityInventory(tabToManage);
                }
            });
        });
    }
}
