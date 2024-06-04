package net.foxelfire.tutorialmod.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider{

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
        .add(ModBlocks.LIGHT_CRYSTAL_BLOCK)
        .add(ModBlocks.LIGHT_LANTERN_BLOCK)
        .add(ModBlocks.PYRITE_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
        .add(ModBlocks.PYRITE_BLOCK)
        .add(ModBlocks.PYRITE_DOOR)
        .add(ModBlocks.PYRITE_PRESSURE_PLATE)
        .add(ModBlocks.PYRITE_SLAB)
        .add(ModBlocks.PYRITE_STAIRS)
        .add(ModBlocks.PYRITE_TRAPDOOR)
        .add(ModBlocks.PYRITE_WALL)
        .add(ModBlocks.PYRITE_BUTTON);
        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_4"))).add(ModBlocks.LIGHT_LANTERN_BLOCK);
        getOrCreateTagBuilder(BlockTags.WALLS).add(ModBlocks.PYRITE_WALL);
        
    }

}
