package net.foxelfire.tutorialmod.block.entity;

import java.util.Optional;

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
import net.minecraft.item.Items;
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


    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    public static final int SHARD_INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int FUEL_INPUT_SLOT = 2;
    public static final int INGREDIENT_SLOT_1 = 3;
    public static final int INGREDIENT_SLOT_2 = 4;
    public static final int INGREDIENT_SLOT_3 = 5;
    protected final PropertyDelegate propertyDelegate;
    private int craftingProgress = 0;
    private int maxProgress = 144;
    private int fuelAmount = 0;
    private int currentFuelTypeAsOrdinal = 0; //this is FUEL_TYPE.NOTHING

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
        if(isSlotAddable(OUTPUT_SLOT)){
            if(this.hasRecipe()){
                if(this.recipeRequiresFuel()){
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

    private boolean isSlotAddable(int slot) { // add int resultAmount to account for crafting 4 items at once
        return this.getStack(slot).isEmpty() || this.getStack(slot).getCount() < this.getStack(slot).getMaxCount();
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<ElementExtractorRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent() && isOutputSlotUnclogged(recipe.get().value().getResult(null));
    }

    private boolean recipeRequiresFuel() {
        return false; // TODO: make a recipe that requires fuel, go into ElementExtractorRecipe
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
        this.removeStack(SHARD_INPUT_SLOT, 1);
        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(), 
        this.getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private void resetCraftingProgress() {
        this.craftingProgress = 0;
    }

    public void useAnyStoredFuel(){
        for (ElementExtractorBlockEntity.FUEL_TYPE fuelType : ElementExtractorBlockEntity.FUEL_TYPE.fuels) {
            if(fuelType != ElementExtractorBlockEntity.FUEL_TYPE.NOTHING && this.getStack(FUEL_INPUT_SLOT).getItem().equals(fuelType.item) && this.fuelAmount <= 1){
                this.currentFuelTypeAsOrdinal = fuelType.ordinal();
                this.removeStack(FUEL_INPUT_SLOT, 1);
                this.fuelAmount = 20;
            }
        }
        if(this.fuelAmount > 0){
            fuelAmount--;
        }
    }
    public enum FUEL_TYPE {
        NOTHING(),
        LIGHT(ModItems.LIGHT_DUST),
        /*AIR("wind_energy"),
        FIRE("heat_energy"),
        EARTH("earth_energy"), add all this later once their sourceItems are done
        WATER("water_energy"),
        PLANT("life_energy"),
        ICE("negative_energy"),
        ELECTRIC("lightning_energy"),
        SOUND("sonic_energy"),*/
        MAGIC(Items.AMETHYST_SHARD);

        int propertyDelegateKey;
        Item item;
        int fuelTextureYCoordinate;
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
                fuels[i].propertyDelegateKey = (i-1);
            }
        }

        FUEL_TYPE (Item associatedItem){
            this.item = associatedItem;
        }

        FUEL_TYPE(){
            this.item = null;
        }

        public int getTextureCoordinate(){
            return this.fuelTextureYCoordinate;
        }
        public static ElementExtractorBlockEntity.FUEL_TYPE getByOrdinal(int number){
            if(!(number > 0 && number < 10)){
                return ElementExtractorBlockEntity.FUEL_TYPE.NOTHING;
            }
            return fuels[number];
        }
        public Item getItem(){
            return this.item;
        }
    }
}
