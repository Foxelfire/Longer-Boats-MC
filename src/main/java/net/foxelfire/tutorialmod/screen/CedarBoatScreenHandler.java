package net.foxelfire.tutorialmod.screen;

import java.util.ArrayList;

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

    private SimpleInventory currentInventory;
    private ArrayList<SimpleInventory> tabs;
    public final CedarBoatEntity entity;

    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (CedarBoatEntity)entity;
        this.tabs = new ArrayList<>(this.entity.getNumberOfChests());
        partitionInventoryList();
        addPlayerInventory(inventory);
        this.switchTab(1);
        inventory.onOpen(inventory.player);
    }

    private void partitionInventoryList(){
        entity.resetInventory();
        for(int i = 0; i < entity.getNumberOfChests(); i++){
            SimpleInventory splitInventory = new SimpleInventory(entity.size());
            for(int j = 0; j < 26; j++){
                TutorialMod.LOGGER.info("Adding Stack Index: " + (j + 26*i));
                splitInventory.setStack(j, entity.getInventory().get(j + 26*i));
            }
            tabs.add(splitInventory);
        }
    }

    public void switchTab(int number){
        number-=1; // accounting for zero-indexing
        if(number >= tabs.size()){
            TutorialMod.LOGGER.error("SimpleInventory index " + number + " is outside the bounds of the inventory tabs " + tabs.size());
            return;
        }
        this.currentInventory = tabs.get(number);
        fillCurrentTab(currentInventory);
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

    private void fillCurrentTab(SimpleInventory inventory){
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) { // same precalculated offsets from rows and columns, just moved up a bit
                this.addSlot(new Slot(inventory, l + i * 9 + 36, 8 + l * 18, 23 + i * 18));
            }
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        /*
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.currentInventory.size()) {
                if (!this.insertItem(originalStack, this.currentInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.currentInventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
        */
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.currentInventory.canPlayerUse(player);
    }

}
