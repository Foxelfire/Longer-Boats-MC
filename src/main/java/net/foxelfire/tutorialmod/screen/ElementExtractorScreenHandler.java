package net.foxelfire.tutorialmod.screen;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ElementExtractorScreenHandler extends ScreenHandler{

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final ElementExtractorBlockEntity blockEntity;

    public ElementExtractorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), 
        new ArrayPropertyDelegate(4));
    }

    public ElementExtractorScreenHandler(int syncId, PlayerInventory inventory, BlockEntity blockEntity, PropertyDelegate propertyDelegate2) {
        super(ModScreenHandlers.ELEMENT_EXTRACTOR_SCREEN_HANDLER, syncId);
        checkSize((Inventory)blockEntity, 4);
        this.inventory = (Inventory)blockEntity;
        inventory.onOpen(inventory.player);
        this.propertyDelegate = propertyDelegate2;
        this.blockEntity = (ElementExtractorBlockEntity)blockEntity;
        fillThisInventory();
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addProperties(propertyDelegate2);
    }

    private void fillThisInventory() {
        final int slotRowYLevel = 16;
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.SHARD_INPUT_SLOT, 99, 36));
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.OUTPUT_SLOT, 80, 64));
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.FUEL_INPUT_SLOT, 61, 36));
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.INGREDIENT_SLOT_1, 61, slotRowYLevel));
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.INGREDIENT_SLOT_2, 80, slotRowYLevel));
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.INGREDIENT_SLOT_3, 99, slotRowYLevel));
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) { // these magic numbers are precalculated
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory inventory){
        for(int i = 0; i < 9; ++i){ 
            // same story with the hotbar here
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // don't look at or try to analyze this, it's literally just for the shift-clicking shortcut.
        // yes, that's all this is. No, it's not a default thing in some BlockEntityScreen interface. 
        // You do have to copy-paste from somewhere every time.
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getArrowScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int arrowHeightInPixels = 26;
        return maxProgress != 0 && progress != 0 ? progress * arrowHeightInPixels / maxProgress : 0;
    }
    public int getScaledFuelRemaining(){
        int fuelRemaining = this.propertyDelegate.get(2);
        // the maximum amount of fuel is 40, and the pixel width of the progress bar is 20, so a simple division by 2 should be fine
        return fuelRemaining != 0 ? fuelRemaining / 2 : 0;
        
    }
    public Item getFuel(){
        int fuel = this.propertyDelegate.get(3);
        if(fuel == -1){
            return Items.AIR;
        }
        try {
            return ElementExtractorBlockEntity.POSSIBLE_FUELS[fuel];
        } catch (ArrayIndexOutOfBoundsException e) {
            TutorialMod.LOGGER.error("fuelIndexProperty is greater than 9, the highest possible fuel value,\nor the 4th property of the PropertyDelegate got corrupted, somehow. Further Info:\n", e);
        }
        return Items.AIR;
    }
}
