package net.foxelfire.tutorialmod.block.entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.recipe.ElementExtractorRecipe;
import net.foxelfire.tutorialmod.screen.ElementExtractorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementExtractorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory{


    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(7, ItemStack.EMPTY);
    public static final int SHARD_INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int FUEL_INPUT_SLOT = 2;
    public static final int INGREDIENT_SLOT_1 = 3;
    public static final int INGREDIENT_SLOT_2 = 4;
    public static final int INGREDIENT_SLOT_3 = 5;
    public static final int INVISIBLE_FUEL_SLOT_FOR_RECIPES = 6;
    protected final PropertyDelegate propertyDelegate;
    private int craftingProgress = 0;
    private int maxProgress = 144;
    private int fuelAmount = 0;
    private int currentFuelTypeAsOrdinal = 0; //this is FUEL_TYPE.NOTHING bc at this point in time i didn't know Optional was a thing
    // and i'm too lazy to refactor this now

    public ElementExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEMENT_EXTRACTOR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() { // client-server syncing bullshit

            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress;
                    case 2 -> ElementExtractorBlockEntity.this.fuelAmount;
                    case 3 -> ElementExtractorBlockEntity.this.currentFuelTypeAsOrdinal;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress = value;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress = value;
                    case 2 -> ElementExtractorBlockEntity.this.fuelAmount = value;
                    case 3 -> ElementExtractorBlockEntity.this.currentFuelTypeAsOrdinal = value;
                }
            }

            @Override
            public int size() {
                return 4; // 4 for four different variables in our delegate
            }
            
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.tutorialmod.element_extractor");
    }

    @Override
    protected void writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("CraftingProgress", craftingProgress);
        nbt.putInt("FuelRemaining", fuelAmount);
        nbt.putInt("TypeOfFuel", currentFuelTypeAsOrdinal);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        nbt.getInt("CraftingProgress");
        nbt.getInt("FuelRemaining");
        nbt.getInt("TypeOfFuel");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity arg2) {
        return new ElementExtractorScreenHandler(syncId, playerInv, this, this.propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockState state, BlockPos pos) {
        if(world.isClient){
            return;
        }
        tryToStoreFuel();
        if(isSlotAddable(OUTPUT_SLOT)){
            if(this.hasRecipe()){
                if(recipeRequiresFuel(getCurrentRecipe())){
                    useAnyStoredFuel();
                }
                this.craftingProgress++;
                markDirty(world, pos, state);

                if(craftingProgress >= maxProgress){
                    this.craftItem();
                    this.resetCraftingProgress();
                }
            } else {
                this.resetCraftingProgress();
            }
        } else {
            this.resetCraftingProgress();
            markDirty(world, pos, state); // marks the block position for chunk saving
        }
    }

    // oh god mc's functional programming-like structure is infecting me
    // yes, this is literally just because i didn't want to copy-paste a foreach loop and a really gross-looking if statement
    // between tryToStoreFuel() and useAnyStoredFuel(). deal with it, future me, past me wanted to feel smart.
    private void doSomethingCoolWithTheFuelThisItemIs(Consumer<ElementExtractorBlockEntity.FUEL_TYPE> typeOperation, 
    boolean additionalCondition){
        for (ElementExtractorBlockEntity.FUEL_TYPE fuel : ElementExtractorBlockEntity.FUEL_TYPE.fuels) {
            if(fuel != ElementExtractorBlockEntity.FUEL_TYPE.NOTHING && this.getStack(FUEL_INPUT_SLOT).getItem().equals(fuel.item) && additionalCondition){
                typeOperation.accept(fuel);
            }
        }
    }

    private void tryToStoreFuel() { // when there's no fuel-needing recipe and the gauge is empty
        doSomethingCoolWithTheFuelThisItemIs(fuel -> {
            setStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES,
            new ItemStack(this.getStack(FUEL_INPUT_SLOT).getItem(), 1));
            this.currentFuelTypeAsOrdinal = fuel.ordinal();
            this.removeStack(FUEL_INPUT_SLOT, 1);
            this.fuelAmount = 20;
        }, this.getStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES).isEmpty());
    }

    public void useAnyStoredFuel(){ // when there is a fuel-needing recipe
        doSomethingCoolWithTheFuelThisItemIs(fuelType -> {
             this.currentFuelTypeAsOrdinal = fuelType.ordinal();
             this.removeStack(FUEL_INPUT_SLOT, 1);
             this.fuelAmount = 20; }, this.fuelAmount <= 1);
         if(this.fuelAmount > 0){
             fuelAmount--;
         } else {
            /* the invis slot effectively has a max capacity of 1 bc that's all we will ever set it to
            *  so we can wipe the whole stack fine
            *  it's only here in the first place so we can tell mc's recipe client/server syncing system to read
            *  the recipe differently if there's an item in this slot, so it's fine to do weird shit like this
            *  bc the player never interacts w/ it so there's no danger of weird edge cases
            */
            this.removeStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES);
         }
     }

    private boolean recipeRequiresFuel(Optional<RecipeEntry<ElementExtractorRecipe>> currentRecipe) {
        return currentRecipe.get().value().requiresFuel();
    }

    private boolean isSlotAddable(int slot) { // add int resultAmount to account for crafting 4 items at once
        return this.getStack(slot).isEmpty() || this.getStack(slot).getCount() < this.getStack(slot).getMaxCount();
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<ElementExtractorRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent() && isOutputSlotUnclogged(recipe.get().value().getResult(null));
    }

    private Optional<RecipeEntry<ElementExtractorRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for(int i = 0; i < this.size(); i++){
            inv.setStack(i, this.getStack(i));
        }
        return getWorld().getRecipeManager().getFirstMatch(ElementExtractorRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean isOutputSlotUnclogged(ItemStack matchingItem) { // add int resultAmount to account for crafting 4 items at once
        return (this.getStack(OUTPUT_SLOT).getItem() == matchingItem.getItem() || this.getStack(OUTPUT_SLOT).isEmpty())
        && (this.getStack(OUTPUT_SLOT).getCount() + matchingItem.getCount()) <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private void craftItem() {
        Optional<RecipeEntry<ElementExtractorRecipe>> recipe = getCurrentRecipe();
        this.removeCorrectIngredientAmounts(recipe);
        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(), 
        this.getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private void removeCorrectIngredientAmounts(Optional<RecipeEntry<ElementExtractorRecipe>> recipe) {
        if(recipe.isPresent()){
            if(!recipe.get().value().requiresFuel()){
                this.removeStack(SHARD_INPUT_SLOT, 1);
            } else {
                Optional<List<Integer>> counts = recipe.get().value().getIngredientCounts();
                int inventoryStartingOffset = INGREDIENT_SLOT_1;
                for (Integer count : counts.get()) {
                    this.removeStack(inventoryStartingOffset, count);
                    inventoryStartingOffset++;
                }
            }
        }
    }

    private void resetCraftingProgress() {
        this.craftingProgress = 0;
    }

    public enum FUEL_TYPE {
        NOTHING(),
        LIGHT(ModItems.LIGHT_DUST, "light_fuel"),
        AIR(ModItems.WIND_DUST, "wind_fuel"),
        FIRE(ModItems.FIRE_DUST, "fire_fuel"),
        EARTH(ModItems.EARTH_DUST, "earth_fuel"),
        WATER(ModItems.WATER_DUST, "water_fuel"),
        PLANT(ModItems.PLANT_DUST, "plant_fuel"),
        ICE(ModItems.ICE_DUST, "ice_fuel"),
        ELECTRIC(ModItems.ELECTRIC_DUST, "electric_fuel"),
        SOUND(ModItems.SOUND_DUST, "sound_fuel"),
        MAGIC(ModItems.ARCANE_DUST, "arcane_fuel");

        private Item item; // states assigned in constructor
        private String id;

        private int fuelTextureYCoordinate;
        static int firstTextureYCoordinate = 26; // beginning of fuel bar textures in pixels of gui/element_extractor.png
        /* ty to https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.3,
         * original idea for defining coordinates this way sparked from reading https://stackoverflow.com/a/66601869.
         * works bc every texture is stored 5 pixels down from each other, meaning we can define everything
         * from the first one by adding 5 repeatedly, which is the same as multiplication. Saves making more instances of stuff
         * if there's a more readable way to just do this in the constructor, fml
         * this is my first time using an enum all by myself, let me be proud of myself for a moment :(
         */
        static ElementExtractorBlockEntity.FUEL_TYPE[] fuels = FUEL_TYPE.values();

        static {
            for(int i = 1; i < fuels.length; i++){
                fuels[i].fuelTextureYCoordinate = (i-1)*5 + firstTextureYCoordinate; // works during first iteration bc 0*5 leaves only the first location - its own coordinate
            }
        }

        FUEL_TYPE (Item associatedItem, String id){
            this.item = associatedItem;
            this.id = id;
        }

        FUEL_TYPE(){
            this.item = null;
            this.id = "not_fueled";
        }

        public int getTextureCoordinate(){
            return this.fuelTextureYCoordinate;
        }
        public static ElementExtractorBlockEntity.FUEL_TYPE getByOrdinal(int number){
            if(!(number > 0 && number < 11)){
                return NOTHING;
            }
            return fuels[number];
        }
        public static ElementExtractorBlockEntity.FUEL_TYPE getByItem(Item item){
            for (FUEL_TYPE fuel : fuels) {
                if(fuel != NOTHING && fuel.item.equals(item)){
                    return fuel;
                }
            }
            return NOTHING;
        }
        public static boolean isFuel(ItemStack stack){
            for (ElementExtractorBlockEntity.FUEL_TYPE fuel : fuels) {
                if(fuel != NOTHING && fuel.item.equals(stack.getItem())){
                    return true;
                }
            }
            return false;
        }
        public Item getItem(){
            return this.item;
        }
        public String getId(){
            return id;
        }
    }
}
