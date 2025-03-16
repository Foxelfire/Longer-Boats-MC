package net.foxelfire.tutorialmod.screen;

import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class CedarBoatScreenHandler extends ScreenHandler {

    public final CedarBoatEntity entity;
    public final PlayerEntity player;
    public final PlayerInventory playerInventory;
    public int currentTab = 0;
    static final SimpleInventory INVENTORY = new SimpleInventory(27);
    public DefaultedList<ItemStack> itemList = DefaultedList.of();
    

    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (CedarBoatEntity)entity;
        this.player = inventory.player;
        this.playerInventory = inventory;
        manageEntityInventory(0);
        inventory.onOpen(inventory.player);
    }

    public void switchTab(int tabIndex){
        if(tabIndex >= entity.getNumberOfChests() || tabIndex < 0){
            return;
        }
        saveEntityInventory(currentTab);
        manageEntityInventory(tabIndex);
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

    @Override
    public void onClosed(PlayerEntity player){
        saveEntityInventory(currentTab);
        manageEntityInventory(0);
        super.onClosed(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(!player.getWorld().isClient()){ // was trying random things to fix this weird cursor stack bug, and wrapping this method in this somehow worked?! Can someone tell me why this works?
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }
  
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < INVENTORY.size()) {
                if (!this.insertItem(originalStack, INVENTORY.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, INVENTORY.size(), false)) {
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
        return INVENTORY.canPlayerUse(player);
    }

    protected void saveEntityInventory(int tab){
        if(player.getWorld().isClient()){
            DefaultedList<ItemStack> previousStacks = INVENTORY.stacks;
            this.entity.sendC2SInventoryPacket(previousStacks, tab);
        }
    }
    
    protected void manageEntityInventory(int tab){
        /* Okay, so when you add a slot, the slot is immediately synced to whatever is in the index of the inventory you assigned it to.
         * Making outside changes to the slot's contents or the inventory index's contents does not work.
         * So in order to make slots reflect the new inventory contents if a button is pressed to move to a new section of
         * the inventory, you have to delete all the current stale slots and make new ones. You can do this by clearing the list of slots (even though said
         * list is final) and re-adding slots to it. The vanilla creative inventory does the exact same thing to handle its tab buttons, so I copied and adapted that
         * logic to fit here.
        */
        DefaultedList<ItemStack> inventoryStacks = this.entity.getInventoryTabAt(tab);
        this.itemList = inventoryStacks;
        this.slots.clear();
        this.addPlayerInventory(this.playerInventory);
        for(int i = 0; i < 27; i++){
            int heightMultiplier = (int)(i/9);
            int xMultiplier = i % 9;
            Slot slot = new Slot(INVENTORY, i, 8 + xMultiplier * 18, 23 + heightMultiplier*18);
            this.addSlot(slot); // vanilla creative inventory uses this.slots.add(), but it doesn't track its ItemStacks
            // to send to an event listener like a block entity like we do. So in order to make sure what's in the slot is properly synced
            // between player, client, and server, we have to use addSlot() to add to the private trackedStacks field in ScreenHandler.
            // Changing this to this.slots.add() causes a game crash. Don't do it. This took me a week to figure out.
        }
        addDisplayArea();
        currentTab = tab;
    }

    private void addDisplayArea(){
        for(int i = 0; i < 27; i++){
            INVENTORY.setStack(i, this.itemList.get(i));
        }
    }

}
