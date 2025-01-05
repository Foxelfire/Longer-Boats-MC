package net.foxelfire.tutorialmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.entity.client.CedarBoatModel;
import net.foxelfire.tutorialmod.entity.client.CedarBoatRenderer;
import net.foxelfire.tutorialmod.entity.client.ModModelLayers;
import net.minecraft.client.render.RenderLayer;

public class TutorialModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CEDAR_SAPLING, RenderLayer.getCutout());
        EntityRendererRegistry.register(ModEntities.CEDAR_BOAT, CedarBoatRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CEDAR_BOAT, CedarBoatModel::getTexturedModelData);
    }
}
