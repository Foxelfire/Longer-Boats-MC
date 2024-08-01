package net.foxelfire.tutorialmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.entity.client.ModModelLayers;
import net.foxelfire.tutorialmod.entity.client.PorcupineModel;
import net.foxelfire.tutorialmod.entity.client.PorcupineRenderer;
import net.foxelfire.tutorialmod.screen.ElementExtractorScreen;
import net.foxelfire.tutorialmod.screen.ModScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TutorialModClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PYRITE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PYRITE_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEWFRUIT_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLOVER_FLOWER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_CLOVER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLOVER, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.PORCUPINE, PorcupineRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PORCUPINE, PorcupineModel::getTexturedModelData);
        HandledScreens.register(ModScreenHandlers.ELEMENT_EXTRACTOR_SCREEN_HANDLER, ElementExtractorScreen::new);
    }
}
