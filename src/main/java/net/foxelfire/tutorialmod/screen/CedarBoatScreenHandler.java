package net.foxelfire.tutorialmod.screen;

import java.util.List;

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
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
    public DefaultedList<ItemStack> itemList = DefaultedList.of();
    public static CedarBoatScreenHandler activeHandler;
    @SuppressWarnings("unused")
    private ItemStack cursorStack = ItemStack.EMPTY;
    @SuppressWarnings("unused")
    private int revision;
    

    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (CedarBoatEntity)entity;
        this.player = inventory.player;
        this.playerInventory = inventory;
        saveEntityInventory(currentTab, currentTab);
        inventory.onOpen(inventory.player);
        activeHandler = this;
    }

    public void switchTab(int tabIndex){
        if(tabIndex >= entity.getNumberOfChests() || tabIndex < 0){
            return;
        }
        saveEntityInventory(currentTab, tabIndex);
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
        saveEntityInventory(currentTab, 0);
        entity.setHasScreen(false);
        super.onClosed(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(!player.getWorld().isClient()){ // was trying random things to fix this weird cursor stack bug, and wrapping this method in a server check somehow worked?! Can someone tell me why this works?
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
    public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
        for (int i = 0; i < stacks.size(); ++i) {
            if(i >= 63){ // this is just a band-aid solution to get this method to cursor stack assignment, until I can fix the problem of the "stacks"
                // list passed into this thing increasing by 63 everytime the client packet listener calls this method due to some server-side stuff before packet sending.
                break;
            }
            this.getSlot(i).setStackNoCallbacks(stacks.get(i));
        }
        this.setCursorStack(cursorStack.copy());
        this.revision = revision;
    }

    protected static void clearStacks(DefaultedList<ItemStack> stacks){
        /* removing from defaulted lists is usually an unsupported operation, but the specific ones
        i'm calling this with have an ArrayList delegate, and ArrayLists support remove(int index), so we're fine!
        so uhhh don't call this with a DefaultedList that doesn't have an ArrayList delegate, though I'm not sure why you would want to... */
        for (int i = 0; i < stacks.size(); i++){
            stacks.remove(i);
        }
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return INVENTORY.canPlayerUse(player);
    }

    protected void saveEntityInventory(int prevTab, int tab){
        if(player.getWorld().isClient()){
            DefaultedList<ItemStack> previousStacks = INVENTORY.stacks;
            this.entity.sendC2SInventoryPacket(previousStacks, prevTab, tab);
        }
    }
    
    protected void manageEntityInventory(int tab){
        /* Okay, so when you add a slot, the slot is immediately synced to whatever is in the index of the inventory you assigned it to.
         * Making outside changes to the slot's contents or the inventory index's contents does not work.
         * So in order to make slots reflect the new inventory contents if a button is pressed to move to a new section of
         * the inventory, you have to delete all the current stale slots and make new ones. You can do this by clearing the list of slots (even though said
         * list is final) and re-adding slots to it. The vanilla creative inventory does the exact same thing to handle its tab buttons, so I copied and adapted that
         * logic to fit here. */
        DefaultedList<ItemStack> inventoryStacks = this.entity.getInventoryTabAt(tab);
        this.itemList = inventoryStacks;
        this.slots.clear();
        clearStacks(this.trackedStacks);
        clearStacks(this.previousTrackedStacks);
        this.addPlayerInventory(this.playerInventory);
        for(int i = 0; i < 27; i++){
            int heightMultiplier = (int)(i/9);
            int xMultiplier = i % 9;
            Slot slot = new Slot(INVENTORY, i, 8 + xMultiplier * 18, 23 + heightMultiplier*18);
            this.addSlot(slot); /* vanilla creative inventory uses this.slots.add(), but it doesn't track its ItemStacks
            to send to an event listener like a block entity like we do. So in order to make sure what's in the slot is properly synced
            between player, client, and server, we have to use addSlot() to add to the private trackedStacks field in ScreenHandler.
            Changing this to this.slots.add() causes a game crash. Don't do it. This took me a week to figure out. */
        }
        addDisplayArea();
        currentTab = tab;
    }

    public static void manageActiveEntityInventory(int tab){
        /* This method is a version of manageEntityInventory without any references to this so it can be called statically. 
         * This is needed so that the chain of back-and-forth client-to-server-to-client packets that saveEntityInventory initiates can
         * figure out which screen handler it needs to manage the inventory of without any information on the currently open screen handler.
         * This method is only meant to be called inside a packet receiver. Switching tabs is done this stinky way for a couple of reasons.
         * Firstly because packet sending, unsurprisingly, runs asynchronously, so just calling the normal manageEntityInventory with
         * saveEntityInventory's second argument in this file can read an outdated entity inventory in getInventoryTabAt() on multiplayer worlds, which can cause
         * a lot of bugs, possibly including an item duplication glitch. Secondly because all of the ways to reach the currently active screen
         * handler directly from the client.execute lambda, like using the player's current screen handler, sadly don't return the instance we want, at least not
         * in a form that we're able to cast to the correct type to call this method. I don't know why and I don't want to know why. */
        if(activeHandler == null){
            return;
        }
        DefaultedList<ItemStack> inventoryStacks = activeHandler.entity.getInventoryTabAt(tab);
        activeHandler.itemList = inventoryStacks;
        activeHandler.slots.clear();
        clearStacks(activeHandler.trackedStacks);
        clearStacks(activeHandler.previousTrackedStacks);
        activeHandler.addPlayerInventory(activeHandler.playerInventory);
        for(int i = 0; i < 27; i++){
            int heightMultiplier = (int)(i/9);
            int xMultiplier = i % 9;
            Slot slot = new Slot(INVENTORY, i, 8 + xMultiplier * 18, 23 + heightMultiplier*18);
            activeHandler.addSlot(slot);
        }
        activeHandler.addDisplayArea();
        activeHandler.currentTab = tab;
    }

    private void addDisplayArea(){
        for(int i = 0; i < 27; i++){
            INVENTORY.setStack(i, this.itemList.get(i));
        }
    }

}
