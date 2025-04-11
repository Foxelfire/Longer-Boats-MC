package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider{

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_ACACIA_ITEM, Blocks.ACACIA_PLANKS.asItem(), Items.ACACIA_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_BAMBOO_ITEM, Blocks.BAMBOO_PLANKS.asItem(), Items.BAMBOO_RAFT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_BIRCH_ITEM, Blocks.BIRCH_PLANKS.asItem(), Items.BIRCH_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_CHERRY_ITEM, Blocks.CHERRY_PLANKS.asItem(), Items.CHERRY_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_DARK_OAK_ITEM, Blocks.DARK_OAK_PLANKS.asItem(), Items.DARK_OAK_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_JUNGLE_ITEM, Blocks.JUNGLE_PLANKS.asItem(), Items.JUNGLE_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_MANGROVE_ITEM, Blocks.MANGROVE_PLANKS.asItem(), Items.MANGROVE_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_OAK_ITEM, Blocks.OAK_PLANKS.asItem(), Items.OAK_BOAT);
        generateBoatRecipe(exporter, ModItems.LONG_BOAT_SPRUCE_ITEM, Blocks.SPRUCE_PLANKS.asItem(), Items.SPRUCE_BOAT);
    }

    private void generateBoatRecipe(RecipeExporter exporter, Item output, Item inputWood, Item inputBoat){
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, output)
        .pattern("*B*")
        .pattern("***")
        .input('*', inputWood)
        .input('B', inputBoat)
        .criterion(hasItem(inputBoat), conditionsFromItem(inputBoat))
        .offerTo(exporter, new Identifier(getRecipeName(inputBoat)));
    }
}
