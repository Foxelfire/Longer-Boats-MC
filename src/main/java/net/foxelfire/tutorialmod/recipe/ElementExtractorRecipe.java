package net.foxelfire.tutorialmod.recipe;

import java.util.List;

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
    private final List<Ingredient> recipeItems;
    private final String requiredFuel;

    public ElementExtractorRecipe(String requiredFuel, List<Ingredient> recipeItems, ItemStack output){
        this.requiredFuel = requiredFuel;
        this.output = output;
        this.recipeItems = recipeItems;
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

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()){
            return false;
        }
        // tests if the item in input slot matches recipe's json index in "ingredients":[]
        // 3:05 in #31 for more info
        if(!recipeRequiresFuel()){
            return recipeItems.get(0).test(inventory.getStack(ElementExtractorBlockEntity.SHARD_INPUT_SLOT));
        }
        return
        recipeItems.get(0).test(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_1))
     && recipeItems.get(1).test(inventory.getStack(ElementExtractorBlockEntity.INGREDIENT_SLOT_2))
     && requiredFuel.equals(ElementExtractorBlockEntity.FUEL_TYPE.getByItem
     (inventory.getStack(ElementExtractorBlockEntity.INVISIBLE_FUEL_SLOT_FOR_RECIPES).getItem()).getId());
    }

    public boolean recipeRequiresFuel() {
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
            ItemStack outputItem = buf.readItemStack(); // wtf are we reading? why are there no arguments?
            // how tf do we know that this buf has a valid ItemStack? we don't. we trust the caller of this. more on this later
            return new ElementExtractorRecipe(requiredFuel, inputItems, outputItem);
        }

        @Override
        public void write(PacketByteBuf buf, ElementExtractorRecipe recipe) {
            buf.writeString(recipe.requiredFuel);
            buf.writeInt(recipe.getIngredients().size()); // this is what we read for size when we make the DefaultedList in read()
            for (Ingredient recipeIngredient : recipe.getIngredients()) {
                recipeIngredient.write(buf);
            }
            buf.writeItemStack(recipe.getResult(null));
            /* promise me, the for loop in read() and the foreach one here are supposed to be looking at the same data.
             * so are buf.read/writeItemStack(). These two methods give data to each other from the client and server.
             * the reason why they don't look anything like each other, sharing zero variable names is because this data is accessed
             * from buffers of presumably json data sent through packets - so the client calls one method, server calls another,
             * and because the buffers here are simply arguments the client/server decides later, there's no way to tell what our data is
             * unless you track down where these methods get called, what is being passed into them, and what data that argument has.
             * All this to say I have zero control or knowledge over how MC uses these.
             * so, while you (future me) can override these just fine, (and in fact you have to bc they don't have defaults in their interface) 
             * you really have to be careful that you're writing and reading only Ingredient instances and an ItemStack,
             * in that order, Ingredients in order of their slots. If not, whatever in MC's source calls these
             * is not going to have a fun time.
             */
        }
    }
}
