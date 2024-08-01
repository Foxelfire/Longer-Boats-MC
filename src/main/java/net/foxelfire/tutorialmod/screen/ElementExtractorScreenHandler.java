package net.foxelfire.tutorialmod.screen;

import net.foxelfire.tutorialmod.block.entity.ElementExtractorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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
        new ArrayPropertyDelegate(2));
    }

    public ElementExtractorScreenHandler(int syncId, PlayerInventory inventory, BlockEntity blockEntity, PropertyDelegate propertyDelegate2) {
        super(ModScreenHandlers.ELEMENT_EXTRACTOR_SCREEN_HANDLER, syncId);
        checkSize((Inventory)blockEntity, 2);
        this.inventory = (Inventory)blockEntity;
        inventory.onOpen(inventory.player);
        this.propertyDelegate = propertyDelegate2;
        this.blockEntity = (ElementExtractorBlockEntity)blockEntity;

        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.INPUT_SLOT, 80, 11)); // (80, 11) on the gui tecture
        this.addSlot(new Slot(this.inventory, ElementExtractorBlockEntity.OUTPUT_SLOT, 80, 59)); 
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addProperties(propertyDelegate2);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
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

    public int getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int arrowWidthInPixels = 26;
        return maxProgress != 0 && progress != 0 ? progress * arrowWidthInPixels / maxProgress : 0;
    }
}
