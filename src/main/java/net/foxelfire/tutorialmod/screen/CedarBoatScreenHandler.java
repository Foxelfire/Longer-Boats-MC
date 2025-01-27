package net.foxelfire.tutorialmod.screen;

import java.util.ArrayList;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CedarBoatScreenHandler extends ScreenHandler{

    private Inventory currentInventory;
    private ArrayList<Inventory> tabs;
    public final CedarBoatEntity entity;

    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (CedarBoatEntity)entity;
        this.tabs = new ArrayList<>();
        partitionInventoryList();
        this.currentInventory = tabs.get(0);
        this.tabs = new ArrayList<>(this.entity.getNumberOfChests());
        inventory.onOpen(inventory.player);
    }

    private void partitionInventoryList(){
        for(int i = 0; i < entity.getNumberOfChests(); i++){
            Inventory splitInventory = new SimpleInventory(27);
            for(int j = 0; j < 26; j++){
                splitInventory.setStack(j, entity.getInventory().get(j*(i+1)));
            }
            tabs.add(splitInventory);
        }
    }

    public void switchTab(int index){
        if(index > tabs.size()){
            TutorialMod.LOGGER.error("Indexed an inventory tab outside of current inventory!");
            return;
        }
        this.currentInventory = tabs.get(index);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
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

    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.currentInventory.canPlayerUse(player);
    }

}
