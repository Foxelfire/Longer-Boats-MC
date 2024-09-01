package net.foxelfire.tutorialmod.block.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.foxelfire.tutorialmod.TutorialMod;
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
    public static final Item[] POSSIBLE_FUELS = {ModItems.LIGHT_DUST, ModItems.WIND_DUST, ModItems.FIRE_DUST,
    ModItems.EARTH_DUST, ModItems.WATER_DUST, ModItems.PLANT_DUST, ModItems.ICE_DUST, ModItems.ELECTRIC_DUST,
    ModItems.SOUND_DUST, ModItems.ARCANE_DUST};
    protected final PropertyDelegate propertyDelegate;
    private int craftingProgress = 0;
    private int maxProgress = 144;
    private int fuelAmount = 0;
    /* "using -1 for empty Optional storing as an int?? stinky code :(" yeah well PropertyDelegates, 
     *  the only way to get a block entity's data onto a server, only can take ints
     *  so there's no standard way to describe an empty value because they can't exist.
     *  I'm deeply upset at this, too, but at least this is a better way to cope with it
     *  than whatever I was doing before.
     */
    private int fuelIndexProperty = getCurrentFuel().isEmpty() ?
    -1 : Arrays.asList(POSSIBLE_FUELS).indexOf(getCurrentFuel().get());

    public ElementExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEMENT_EXTRACTOR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress;
                    case 2 -> ElementExtractorBlockEntity.this.fuelAmount;
                    case 3 -> ElementExtractorBlockEntity.this.fuelIndexProperty;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress = value;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress = value;
                    case 2 -> ElementExtractorBlockEntity.this.fuelAmount = value;
                    case 3 -> ElementExtractorBlockEntity.this.fuelIndexProperty = value;
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
        nbt.putInt("TypeOfFuel", fuelIndexProperty);
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

    private Optional<Item> getCurrentFuel(){
        // Items.AIR is the value of getItem() used on any empty ItemStack/empty slot,
        // and it's not a possible fuel, so we don't need to check if empty
        if(Arrays.asList(POSSIBLE_FUELS).contains(getStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES).getItem())){
            return Optional.of(getStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES).getItem());
        } else {
            return Optional.empty();
        }
    }

    public void tick(World world, BlockState state, BlockPos pos) {
        if(world.isClient){
            return;
        }
        storeFuel();
        if(isSlotAddable(OUTPUT_SLOT)){
            if(this.hasRecipe()){
                if(recipeRequiresFuel(getCurrentRecipe())){
                    useFuel();
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

    private void useFuel() {
        if(fuelAmount > 1 || (this.getStack(FUEL_INPUT_SLOT).isEmpty() && fuelAmount == 1)){
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

    private void storeFuel(){
        if(this.getStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES).isEmpty() && Arrays.asList(POSSIBLE_FUELS).contains(this.getStack(FUEL_INPUT_SLOT).getItem())){
            setStack(INVISIBLE_FUEL_SLOT_FOR_RECIPES, new ItemStack(this.getStack(FUEL_INPUT_SLOT).getItem(), 1));
            refillFuel();
        }
    }

    private void refillFuel(){
        updateFuelItem();
        this.removeStack(FUEL_INPUT_SLOT, 1);
        this.fuelAmount = 40;
    }

    private void updateFuelItem(){
        this.fuelIndexProperty = getCurrentFuel().isEmpty() ?
        -1 : Arrays.asList(POSSIBLE_FUELS).indexOf(getCurrentFuel().get());
    }

    private boolean recipeRequiresFuel(Optional<RecipeEntry<ElementExtractorRecipe>> currentRecipe) {
        return currentRecipe.get().value().getRequiredFuel().isPresent();
    }

    private boolean isSlotAddable(int slot) {
        return this.getStack(slot).isEmpty() || this.getStack(slot).getCount() < this.getStack(slot).getMaxCount();
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<ElementExtractorRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent() && isOutputSlotUnclogged(recipe.get().value().getResult(null));
    }

    private Optional<RecipeEntry<ElementExtractorRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for(int i = 0; i < this.size(); i++){
            if(i != FUEL_INPUT_SLOT){
                inv.setStack(i, this.getStack(i));
            }
        }
        return getWorld().getRecipeManager().getFirstMatch(ElementExtractorRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean isOutputSlotUnclogged(ItemStack matchingItem) {
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
            if(!recipeRequiresFuel(recipe)){
                this.removeStack(SHARD_INPUT_SLOT, 1);
            } else {
                Optional<List<Integer>> counts = recipe.get().value().getIngredientCounts();
                Supplier<Integer> inventoryStartingOffset = () -> { // to get to the ingredient slots, which are listed in the middle
                    for(int i = 0; i < 3; i++){
                        if(!inventory.get(i+3).isEmpty()){
                            return i+3;
                        }
                    } // all the slots are empty??? then how can this recipe be valid? that's a bigger issue
                    TutorialMod.LOGGER.error("A Recipe of type tutorialmod:element_extracting cannot have no ingredients");
                    return 0;
                };
                int iso = inventoryStartingOffset.get();
                for (Integer count : counts.get()) {
                    this.removeStack(iso, count);
                    iso++;
                }
            }
        }
    }

    private void resetCraftingProgress() {
        this.craftingProgress = 0;
    }
}
