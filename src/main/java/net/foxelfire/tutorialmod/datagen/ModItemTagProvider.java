package net.foxelfire.tutorialmod.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.util.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

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
        .add(ModItems.DEWFRUIT_SEEDS)
        .add(Items.NETHER_WART);
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
        .add(ModItems.PYRITE_LEGGINGS)
        .add(ModItems.PYRITE_BOOTS)
        .add(ModItems.PYRITE_HELMET)
        .add(ModItems.PYRITE_CHESTPLATE);
        getOrCreateTagBuilder(ItemTags.SNIFFER_FOOD)
        .add(ModItems.DEWFRUIT_SEEDS)
        .add(ModItems.DEWFRUIT);
        // Blocks w/ flower-related BlockTags don't have their item forms automatically added to the corresponding ItemTag, 
        // even tho they're still ItemConvertibles which makes doing that easy??? average lazy mojang - fine, i'll do it myself
        getOrCreateTagBuilder(ItemTags.SMALL_FLOWERS).add(ModBlocks.CLOVER_FLOWER.asItem());
        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS).add(ModItems.BAR_BRAWL_MUSIC_DISC);
        getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(ModItems.BAR_BRAWL_MUSIC_DISC);
    }

}
