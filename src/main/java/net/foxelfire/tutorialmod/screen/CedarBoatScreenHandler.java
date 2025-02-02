package net.foxelfire.tutorialmod.screen;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CedarBoatScreenHandler extends ScreenHandler{

    private SimpleInventory frontendInventory = new SimpleInventory(27);
    public final CedarBoatEntity entity;
    

    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (CedarBoatEntity)entity;
        addScreenInventory(frontendInventory);
        addPlayerInventory(inventory);
        this.switchTab(1);
        inventory.onOpen(inventory.player);
    }

    public void switchTab(int number){
        number-=1; // accounting for zero-indexing
        if(number >= entity.getNumberOfChests()){
            TutorialMod.LOGGER.error("SimpleInventory index " + number + " is outside the bounds of the inventory tabs " + entity.getNumberOfChests());
            return;
        }
        entity.setActiveInventory(number);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) { // these magic numbers are precalculated
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 89 + i * 18));
            }
        } // hotbar
        for(int i = 0; i < 9; ++i){ 
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 147));
        }
    }

    private void addScreenInventory(SimpleInventory inventory){
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) { // same precalculated offsets from rows and columns, just moved up a bit
                this.addSlot(new Slot(inventory, l + i * 9, 8 + l * 18, 23 + i * 18));
                TutorialMod.LOGGER.info("slot id: " + (l + i * 9));
            }
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.frontendInventory.size()) {
                if (!this.insertItem(originalStack, this.frontendInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.frontendInventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
       // return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.frontendInventory.canPlayerUse(player);
    }

}
