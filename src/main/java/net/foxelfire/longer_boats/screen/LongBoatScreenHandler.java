package net.foxelfire.longer_boats.screen;

import java.util.List;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.util.EntityIdPayload;
import net.foxelfire.longer_boats.util.TabSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class LongBoatScreenHandler extends ScreenHandler {

    public AbstractLongBoatEntity entity;
    public final PlayerInventory playerInventory;
    private final SimpleInventory dummyInventory = new SimpleInventory(27);
    private int currentTab = 0;

    public LongBoatScreenHandler(int syncId, PlayerInventory inventory, EntityIdPayload payload){
        this(syncId, inventory, (AbstractLongBoatEntity)inventory.player.getWorld().getEntityById(payload.entityID()));
    }

    public LongBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.LONG_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (AbstractLongBoatEntity)entity;
        this.playerInventory = inventory;

        addPlayerInventory(this.playerInventory);
        addEntityInventory(this.entity);

        inventory.onOpen(inventory.player);
    }

    public int getCurrentTab(){
        return currentTab;
    }

    public void setCurrentTab(int tab){
        if(tab >= this.entity.getFullInventory().size() || tab <= -1){
            return;
        }
        currentTab = tab;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) { // these magic numbers are precalculated, they're coordinates for slot positions
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 89 + i * 18));
            }
        } // and the hotbar
        for(int i = 0; i < 9; ++i){ 
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 147));
        }
    }

    private void addEntityInventory(AbstractLongBoatEntity entity){
        for(int i = 0; i < 27; i++){
            int heightMultiplier = (int)(i/9);
            int xMultiplier = i % 9;
            TabSlot slot = new TabSlot(entity, this, i, dummyInventory, 8 + xMultiplier * 18, 23 + heightMultiplier*18);
            this.addSlot(slot);
        }
    }

    @Override
    public void onClosed(PlayerEntity player){
        entity.setHasScreen(false);
        super.onClosed(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(!player.getWorld().isClient()){
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }

    @Override
    public boolean isValid(int slotIndex){
        if(!(slotIndex == -1 || slotIndex == -999 || slotIndex < this.slots.size())){
            LongerBoatsMod.LOGGER.info("Invalid slotIndex " + slotIndex + " Size: " + this.slots.size());
        }
        return slotIndex == -1 || slotIndex == -999 || slotIndex < this.slots.size();
    }
  
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // this was copy-pasted from a tutorial, no clue how this works
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.entity.getTab(this.getCurrentTab()).size()) {
                if (!this.insertItem(originalStack, this.entity.getTab(this.getCurrentTab()).size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.entity.getTab(this.getCurrentTab()).size(), false)) {
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
        return true;
    }
}
