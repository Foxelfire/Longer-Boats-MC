package net.foxelfire.tutorialmod.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class ElementExtractorRecipe implements Recipe<SimpleInventory>{

    private final ItemStack output;
    private final Optional<List<Integer>> recipeCounts;
    private final List<Ingredient> recipeItems;
    private final String requiredFuel;

    // TODO: after count storage is done, work on empty space checking using Ingredient.EMPTY
    public ElementExtractorRecipe(String requiredFuel, List<Ingredient> recipeItems, Optional<List<Integer>> recipeCounts, ItemStack output){
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

    public String getRequiredFuel(){
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
        if(!requiresFuel()){ // yes, all non-fuel recipes require the shard slot...
            return recipeItems.get(0).test(inventory.getStack(ElementExtractorBlockEntity.SHARD_INPUT_SLOT));
        }
        return inventoryMatches(inventory)
     && requiredFuel.equals(ElementExtractorBlockEntity.FUEL_TYPE.getByItem
     (inventory.getStack(ElementExtractorBlockEntity.INVISIBLE_FUEL_SLOT_FOR_RECIPES).getItem()).getId());
    }

    private boolean inventoryMatches(SimpleInventory inventory){

        // ty to this post for helping me make an easy filter: https://stackoverflow.com/questions/2955043/predicate-in-java
        // don't question it i'm running on no sleep currently
        Predicate<Ingredient> isFuel = new Predicate<Ingredient>(){
            @Override
            public boolean test(Ingredient t) {
                for (ItemStack stack : t.getMatchingStacks()) {
                    if(ElementExtractorBlockEntity.FUEL_TYPE.isFuel(stack)){
                        return true;
                    }
                }
                return false;
            }
            
        };
        DefaultedList<ItemStack> emptyList = DefaultedList.ofSize(3, ItemStack.EMPTY);

        List<ItemStack> inputSubInventory = new ArrayList<ItemStack>();
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_1));
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_2));
        inputSubInventory.add(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_3));
        inputSubInventory.removeAll(emptyList);

        List<Ingredient> mutableRecipeItems = new ArrayList<Ingredient>();
        mutableRecipeItems.addAll(recipeItems);
        mutableRecipeItems.removeIf(isFuel);
    
        int positionInRecipe = 0;
        for(int i = 0; i < 3; i++){
            if((!inventory.getStack(i+3).isEmpty() || i==2) && inputSubInventory.size() == mutableRecipeItems.size()){
                if(positionInRecipe < mutableRecipeItems.size()){
                    if(mutableRecipeItems.get(positionInRecipe).test(inputSubInventory.get(positionInRecipe))){
                        positionInRecipe++;
                    } else {
                        positionInRecipe = 0;
                    }
                }
            } else {
                positionInRecipe = 0;
            }
        }
        return positionInRecipe == mutableRecipeItems.size();
    }

    public boolean requiresFuel() {
       return !(requiredFuel.equals("not_fueled"));
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
                Codec.STRING
                .fieldOf("fuel_type_required")
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
            return Codecs.validate // codec obtained by validating other codec, validator function for it
                (Codecs.validate( // codec, validator function
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

        @Override
        public ElementExtractorRecipe read(PacketByteBuf buf) {
            String requiredFuel = buf.readString();
            DefaultedList<Ingredient> inputItems = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for(int i = 0; i < inputItems.size(); i++){ // getting our recipe over from client to server
                inputItems.set(i, Ingredient.fromPacket(buf));
            }
            Optional<List<Integer>> recipeCounts = buf.readOptional(null);
            ItemStack outputItem = buf.readItemStack(); // wtf are we reading? why are there no arguments?
            // how tf do we know that this buf has a valid ItemStack? we don't. we trust the caller of this. more on this later
            return new ElementExtractorRecipe(requiredFuel, inputItems, recipeCounts, outputItem);
        }

        @Override
        public void write(PacketByteBuf buf, ElementExtractorRecipe recipe) {
            buf.writeString(recipe.requiredFuel);
            buf.writeInt(recipe.getIngredients().size()); // this is what we read for size when we make the DefaultedList in read()
            for (Ingredient recipeIngredient : recipe.getIngredients()) {
                recipeIngredient.write(buf);
            }
            buf.writeOptional(recipe.recipeCounts, null);
            buf.writeItemStack(recipe.getResult(null));
            /* promise me, the for loop in read() and the foreach one here are supposed to be looking at the same data.
             * so are buf.read/writeItemStack(). These two methods give data to each other from the client and server.
             * the reason why they don't look anything like each other, sharing zero variable names is because this data is accessed
             * from buffers of presumably json data sent through packets - so the client calls one method, server calls another,
             * and because the buffers here are simply arguments the client/server decides later, there's no way to tell what our data is
             * unless you track down where these methods get called, what is being passed into them, and what data that argument has.
             * All this to say I have zero control or knowledge over how MC uses these.
             * so, while you (future me) can override these just fine, (and in fact you have to bc they don't have defaults in their interface) 
             * you really have to be careful that you're writing and reading the same things, in the same order.
             */
        }
    }
}
