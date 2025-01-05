package net.foxelfire.tutorialmod.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider{

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(ItemTags.PLANKS).add(ModBlocks.CEDAR_PLANKS.asItem());
        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
        .add(ModBlocks.CEDAR_LOG.asItem())
        .add(ModBlocks.CEDAR_WOOD.asItem())
        .add(ModBlocks.STRIPPED_CEDAR_LOG.asItem())
        .add(ModBlocks.STRIPPED_CEDAR_WOOD.asItem());

    }

}
