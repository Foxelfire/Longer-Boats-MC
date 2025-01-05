package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.data.server.recipe.RecipeExporter;

public class ModRecipeProvider extends FabricRecipeProvider{
    

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.CEDAR_LOG, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.STRIPPED_CEDAR_LOG, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.CEDAR_WOOD, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.STRIPPED_CEDAR_WOOD, "cedar_planks_recipes", 4);
        offerBoatRecipe(exporter, ModItems.CEDAR_BOAT_ITEM, ModBlocks.CEDAR_WOOD);
    }
}
