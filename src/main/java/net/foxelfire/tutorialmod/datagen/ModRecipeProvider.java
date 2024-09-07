package net.foxelfire.tutorialmod.datagen;

import java.util.List;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.util.ModTags;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider{
    
    private static List<Item> DYEABLE_KONPEITO = List.of(ModItems.BLACK_KONPEITO, ModItems.BLUE_KONPEITO, 
    ModItems.BROWN_KONPEITO, ModItems.CYAN_KONPEITO, ModItems.GRAY_KONPEITO, ModItems.GREEN_KONPEITO, 
    ModItems.LIGHT_BLUE_KONPEITO, ModItems.LIGHT_GRAY_KONPEITO, ModItems.LIME_KONPEITO, ModItems.MAGENTA_KONPEITO, 
    ModItems.ORANGE_KONPEITO, ModItems.PINK_KONPEITO, ModItems.PURPLE_KONPEITO, ModItems.RED_KONPEITO, 
    ModItems.YELLOW_KONPEITO, ModItems.KONPEITO);

    private static List<Item> DYES = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE,
    Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE,
    Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);

    private static final List<ItemConvertible> SMELT_TO_PYRITE_INGOT = List.of(ModItems.PYRITE); // bc offerSmelting needs a list apparently
    

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerSmelting(exporter, SMELT_TO_PYRITE_INGOT, RecipeCategory.MISC, ModItems.PYRITE_INGOT, 0.5f, 150, getName());
        offerBlasting(exporter, SMELT_TO_PYRITE_INGOT, RecipeCategory.MISC, ModItems.PYRITE_INGOT, 0.5f, 75, getName());
        offerDyeableFoodRecipes(exporter, DYES, DYEABLE_KONPEITO, getName());
        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_CRYSTAL_BLOCK, ModItems.LIGHT_SHARD);
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.PYRITE_INGOT, RecipeCategory.DECORATIONS, ModBlocks.PYRITE_BLOCK);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PYRITE_SLAB, ModBlocks.PYRITE_BLOCK);
        offerPressurePlateRecipe(exporter, ModBlocks.PYRITE_PRESSURE_PLATE, ModItems.PYRITE_INGOT);
        offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PYRITE_WALL, ModBlocks.PYRITE_BLOCK);
        offerShapelessRecipe(exporter, ModBlocks.PYRITE_BUTTON, ModItems.PYRITE_INGOT, null, 1);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.CEDAR_LOG, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.STRIPPED_CEDAR_LOG, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.CEDAR_WOOD, "cedar_planks_recipes", 4);
        offerShapelessRecipe(exporter, ModBlocks.CEDAR_PLANKS, ModBlocks.STRIPPED_CEDAR_WOOD, "cedar_planks_recipes", 4);
        
        createStairsRecipe(ModBlocks.PYRITE_STAIRS, Ingredient.ofItems(ModItems.PYRITE_INGOT))
        .criterion(hasItem(ModBlocks.PYRITE_BLOCK), conditionsFromItem(ModBlocks.PYRITE_BLOCK))
        .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PYRITE_STAIRS)));

        createDoorRecipe(ModBlocks.PYRITE_DOOR, Ingredient.ofItems(ModItems.PYRITE_INGOT))
        .criterion(hasItem(ModBlocks.PYRITE_BLOCK), conditionsFromItem(ModBlocks.PYRITE_BLOCK))
        .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PYRITE_DOOR)));

        createTrapdoorRecipe(ModBlocks.PYRITE_TRAPDOOR, Ingredient.ofItems(ModItems.PYRITE_INGOT))
        .criterion(hasItem(ModBlocks.PYRITE_BLOCK), conditionsFromItem(ModBlocks.PYRITE_BLOCK))
        .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PYRITE_TRAPDOOR)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.KONPEITO, 4)
        .pattern("*.*") // foxel using builder indenting? no wayyyyy... only so that it more resembles the structure of the resulting json file so we can edit it easier
        .pattern("*L*")
        .pattern("***")
        .input('*', Items.SUGAR)
        .input('.', ModTags.Items.KONPEITO_SEED_CORES)
        .input('L', ModItems.LIGHT_SHARD)
        .criterion(hasItem(ModItems.LIGHT_SHARD), conditionsFromItem(ModItems.LIGHT_SHARD))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.KONPEITO)));


        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PYRITE_PICKAXE, 1).pattern("PPP").pattern(" / ").pattern(" / ")
        .input('/', Items.STICK).input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_PICKAXE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PYRITE_AXE, 1).pattern("PP").pattern("/P").pattern("/ ")
        .input('/', Items.STICK).input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_AXE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PYRITE_SWORD, 1).pattern("P").pattern("P").pattern("/")
        .input('/', Items.STICK).input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_SWORD)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PYRITE_SHOVEL, 1).pattern("P").pattern("/").pattern("/")
        .input('/', Items.STICK).input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_SHOVEL)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PYRITE_HOE, 1).pattern("PP").pattern("/ ") .pattern("/ ")
        .input('/', Items.STICK).input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_HOE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.PYRITE_HELMET, 1)
        .pattern("PPP").pattern("P P")
        .input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_HELMET)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.PYRITE_CHESTPLATE, 1)
        .pattern("P P").pattern("PPP").pattern("PPP")
        .input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_CHESTPLATE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.PYRITE_LEGGINGS, 1)
        .pattern("PPP").pattern("P P").pattern("P P")
        .input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_LEGGINGS)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.PYRITE_BOOTS, 1)
        .pattern("P P").pattern("P P")
        .input('P', ModItems.PYRITE_INGOT)
        .criterion(hasItem(ModItems.PYRITE_INGOT), conditionsFromItem(ModItems.PYRITE_INGOT))
        .offerTo(exporter, new Identifier(getRecipeName(ModItems.PYRITE_BOOTS)));
    }
    // this recipe is stolen from minecraft's RecipeProvider, it's just the dyeable recipes one but with the RecipeCategory of FOOD instead of BUILDING_BLOCKS
    private static void offerDyeableFoodRecipes(RecipeExporter exporter, List<Item> dyes, List<Item> dyeables, String group) {
        for (int i = 0; i < dyes.size(); ++i) {
            Item item = dyes.get(i);
            Item item2 = dyeables.get(i);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, item2).input(item).input(Ingredient.ofStacks(dyeables.stream().filter(dyeable -> !dyeable.equals(item2)).map(ItemStack::new))).group(group).criterion("has_needed_dye", RecipeProvider.conditionsFromItem(item)).offerTo(exporter, "dye_" + RecipeProvider.getItemPath(item2));
        }
    }

}
