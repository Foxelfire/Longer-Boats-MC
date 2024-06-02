package net.foxelfire.tutorialmod.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.foxelfire.tutorialmod.util.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider{

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(ModTags.Items.KONPEITO_SEED_CORES)
        .add(Items.MELON_SEEDS)
        .add(Items.WHEAT_SEEDS)
        .add(Items.PUMPKIN_SEEDS)
        .add(Items.BEETROOT_SEEDS)
        .add(Items.TORCHFLOWER_SEEDS)
        .add(Items.NETHER_WART);
    }

}
