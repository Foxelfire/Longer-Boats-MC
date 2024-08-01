package net.foxelfire.tutorialmod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.screen.ElementExtractorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementExtractorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory{

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    /* naming all our inventory slots in this block entity to remember which slot corresponds to which index
     * add more when we get more
     */
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    protected final PropertyDelegate propertyDelegate;
    private int craftingProgress = 0;
    private int maxProgress = 72;

    public ElementExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEMENT_EXTRACTOR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() { // client-server syncing bullshit

            @Override
            public int get(int index) {
                return switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index){
                    case 0 -> ElementExtractorBlockEntity.this.craftingProgress = value;
                    case 1 -> ElementExtractorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2; // 2 for two different client-server synced variables - craftingProgress and maxProgress
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
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        nbt.getInt("CraftingProgress");
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
        if(isOutputSlotAddable()){
            if(this.hasRecipe()){
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
            markDirty(world, pos, state);
        }
    }

    private boolean isOutputSlotAddable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean hasRecipe() {
        ItemStack result = new ItemStack(ModItems.LIGHT_DUST);
        boolean hasCorrectInput = (this.getStack(INPUT_SLOT).getItem() == ModItems.LIGHT_SHARD);
        return hasCorrectInput && isOutputSlotFree(result);

    }

    private boolean isOutputSlotFree(ItemStack matchingItem) {
        return (this.getStack(OUTPUT_SLOT).getItem() == matchingItem.getItem() || this.getStack(OUTPUT_SLOT).isEmpty())
        && (this.getStack(OUTPUT_SLOT).getCount() + matchingItem.getCount()) <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private void craftItem() {
        this.removeStack(INPUT_SLOT, 1);
        ItemStack result = new ItemStack(ModItems.LIGHT_DUST);
        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), this.getStack(OUTPUT_SLOT).getCount() + 1));
    }

    private void resetCraftingProgress() {
        this.craftingProgress = 0;
    }

}
