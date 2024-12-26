package net.foxelfire.tutorialmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.entity.client.CedarBoatModel;
import net.foxelfire.tutorialmod.entity.client.CedarBoatRenderer;
import net.foxelfire.tutorialmod.entity.client.ModModelLayers;
import net.foxelfire.tutorialmod.entity.client.PorcupineModel;
import net.foxelfire.tutorialmod.entity.client.PorcupineRenderer;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.foxelfire.tutorialmod.screen.ElementExtractorScreen;
import net.foxelfire.tutorialmod.screen.ModScreenHandlers;
import net.foxelfire.tutorialmod.util.ModNetworkingConstants;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TutorialModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PYRITE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PYRITE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEWFRUIT_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLOVER_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_CLOVER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLOVER, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.PORCUPINE, PorcupineRenderer::new);
        EntityRendererRegistry.register(ModEntities.CEDAR_BOAT, CedarBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PORCUPINE, PorcupineModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CEDAR_BOAT, CedarBoatModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.ELEMENT_EXTRACTOR_SCREEN_HANDLER, ElementExtractorScreen::new);
        ServerPlayNetworking.registerGlobalReceiver(ModNetworkingConstants.BOAT_MOVEMENT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                int entityId = buf.readInt();
                float yawVelocity = buf.readFloat();
                Vec3d velocity = buf.readVec3d();
                boolean isPlayerInputting = buf.readBoolean();
                CedarBoatEntity boat = (CedarBoatEntity)((ServerWorld)(player.getWorld())).getEntityById(entityId);
                if(boat != null){
                    boat.setYaw(boat.getYaw() + yawVelocity);
                    boat.updateVelocity(1, velocity);
                    boat.setPlayer1Inputting(isPlayerInputting);
                }
            });
        });
    }
}
