package net.foxelfire.tutorialmod.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class ElementExtractorRecipe implements Recipe<SimpleInventory>{

    private final ItemStack output;
    private final Optional<List<Integer>> recipeCounts;
    private final List<Ingredient> recipeItems;
    private final Optional<Item> requiredFuel;

    public ElementExtractorRecipe(Optional<Item> requiredFuel, List<Ingredient> recipeItems, Optional<List<Integer>> recipeCounts, ItemStack output){
        this.requiredFuel = requiredFuel;
        this.output = output;
        this.recipeItems = recipeItems;
        this.recipeCounts = recipeCounts;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return output;
    }

    public Optional<Item> getRequiredFuel(){
        return requiredFuel;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients(){
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.recipeItems.size());
        list.addAll(recipeItems);
        return list;
    }

    public Optional<List<Integer>> getIngredientCounts(){
        return this.recipeCounts;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()){
            return false;
        }
        // tests if the item in input slot matches recipe's json index in "ingredients":[]
        // 3:05 in #31 for more info
        if(requiredFuel.isEmpty()){ // yes, all non-fuel recipes require the shard slot...
            return recipeItems.get(0).test(inventory.getStack(ElementExtractorBlockEntity.SHARD_INPUT_SLOT));
        }
        return inventoryMatches(inventory)
     && requiredFuel.get().equals(inventory.getStack(ElementExtractorBlockEntity.INVISIBLE_FUEL_SLOT_FOR_RECIPES).getItem());
    }

    private boolean inventoryMatches(SimpleInventory inventory){

        List<ItemStack> inputSubInventory = new ArrayList<ItemStack>();
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_1));
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_2));
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_3));
        int recipeItemsProcessed = 0;

        for(int i = 0; i < inputSubInventory.size(); i++){
            if(!inputSubInventory.get(i).isEmpty()){
                recipeItemsProcessed++; 
                // only start checking for matches between recipeItems and the inv once you skip the empty, potentially padding slots
                // lists being zero indexed is normally helpful but not here, also make sure early there aren't more items than expected
                if(recipeItemsProcessed > recipeItems.size() || !recipeItems.get(recipeItemsProcessed-1).test(inputSubInventory.get(i))){
                    return false;
                }
            } else if(recipeItemsProcessed < recipeItems.size() && recipeItemsProcessed != 0){ 
                return false; // we haven't finished checking for matches, but the next slot is empty? Oh no, a gap in the middle!
            }
        }

        if(recipeItemsProcessed == recipeItems.size()){ // we're finished checking all the items and there's no problems!
            return true;
        }
        return false; // there weren't enough items to check, they just gave us the first few ingredients and left us hanging!
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ElementExtractorRecipe>{ // RecipeType will automatically register our type, i think
        public static final Type INSTANCE = new Type();
        public static final String ID = "element_extracting";
    }

    public static class Serializer implements RecipeSerializer<ElementExtractorRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "element_extracting";

        // Tutorial creator has no clue how a codec works so make it one of your final goals to learn them
        // so that you eventually know how to do whatever this is better
        // SIKE: IT'S OUR GOAL RN! go to https://docs.fabricmc.net/develop/codecs to make this little shit
        // so u can prove you're better than him /j
        public static final Codec<ElementExtractorRecipe> CODEC = RecordCodecBuilder.create
            (recipeInstance -> recipeInstance.group(
                Registries.ITEM.getCodec()
                .optionalFieldOf("fuel_type_required")
                .forGetter(ElementExtractorRecipe::getRequiredFuel),

                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC, 9)
                    .fieldOf("ingredients")
                    .forGetter(ElementExtractorRecipe::getIngredients)
                ,
                Codec.INT
                .listOf()
                .optionalFieldOf("ingredient_counts")
                .forGetter(ElementExtractorRecipe::getIngredientCounts),

                RecipeCodecs.CRAFTING_RESULT
                    .fieldOf("output")
                    .forGetter(r -> r.output)

            ).apply(recipeInstance, ElementExtractorRecipe::new));

        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate, int max) {
            return Codecs.validate // yeah im just gonna admit i have no clue what this does.
                (Codecs.validate(
                delegate.listOf(),
                list -> list.size() > max ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)
                ), 
                list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list)
            );
        }

        @Override
        public Codec<ElementExtractorRecipe> codec() {
            return CODEC;
        }

        // these two methods get our recipe's info from client to server
        @Override
        public ElementExtractorRecipe read(PacketByteBuf buf) {
            Optional<Item> requiredFuel = buf.readOptional(null);
            DefaultedList<Ingredient> inputItems = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for(int i = 0; i < inputItems.size(); i++){
                inputItems.set(i, Ingredient.fromPacket(buf));
            }
            Optional<List<Integer>> recipeCounts = buf.readOptional(null);
            ItemStack outputItem = buf.readItemStack();
            return new ElementExtractorRecipe(requiredFuel, inputItems, recipeCounts, outputItem);
        }

        @Override
        public void write(PacketByteBuf buf, ElementExtractorRecipe recipe) {
            buf.writeOptional(recipe.requiredFuel, null);
            buf.writeInt(recipe.getIngredients().size()); // this is what we read for size when we make the DefaultedList in read()
            for (Ingredient recipeIngredient : recipe.getIngredients()) {
                recipeIngredient.write(buf);
            }
            buf.writeOptional(recipe.recipeCounts, null);
            buf.writeItemStack(recipe.getResult(null));
            // make sure you read and write the same things!
        }
    }
}
