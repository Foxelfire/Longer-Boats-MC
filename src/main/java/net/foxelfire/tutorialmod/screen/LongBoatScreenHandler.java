package net.foxelfire.tutorialmod.screen;

import java.util.List;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.AbstractLongBoatEntity;
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

public class LongBoatScreenHandler extends ScreenHandler {

    public AbstractLongBoatEntity entity;
    public final PlayerEntity player;
    public final PlayerInventory playerInventory;
    public int currentTab = 0;
    static final SimpleInventory INVENTORY = new SimpleInventory(27);
    private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
    private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
    public DefaultedList<ItemStack> itemList = DefaultedList.of();
    public static LongBoatScreenHandler activeHandler;
    @SuppressWarnings("unused")
    private int revision;
    

    public LongBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getVehicle());
    }
        
    public LongBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
        super(ModScreenHandlers.LONG_BOAT_SCREEN_HANDLER, syncId);
        this.entity = (AbstractLongBoatEntity)entity;
        this.player = inventory.player;
        this.playerInventory = inventory;
        activeHandler = this;
        manageEntityInventory(currentTab); // so that we don't accidentally copy our old inventory from a previous entity to a new one
        if(player.getWorld().isClient()){
            saveEntityInventory(currentTab, currentTab);
        }
        inventory.onOpen(inventory.player);
    }

    public void switchTab(int tabIndex){
        if(tabIndex >= entity.getNumberOfChests() || tabIndex < 0){
            return;
        }
        saveEntityInventory(currentTab, tabIndex);
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

    @Override
    public void onClosed(PlayerEntity player){
        saveEntityInventory(currentTab, 0);
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
            TutorialMod.LOGGER.info("Invalid slotIndex " + slotIndex + " Size: " + this.slots.size());
        }
        return slotIndex == -1 || slotIndex == -999 || slotIndex < this.slots.size();
    }
  
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // this was copy-pasted from a tutorial, no clue how this works
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
            if(i >= 63){ // this is just a band-aid solution to get this method to cursor stack assignment, the real problem is that the "stacks"
                // list passed into this thing increases by 63 everytime the client packet listener calls this method due to DefaultedList removing
                // from its ArrayList delegate (more on that later) not actually removing anything  - didn't want to waste time
                // reverse-engineering more stuff than I technically need to so I just left this here
                break;
            }
            this.getSlot(i).setStackNoCallbacks(stacks.get(i));
        }
        this.setCursorStack(cursorStack.copy());
        this.revision = revision;
    }

    protected static void clearStacks(DefaultedList<ItemStack> stacks){
        /* Fabric API docs say remove() on DefaultedLists is always an unsupported operation, but the specific ones
        i'm calling this with have an ArrayList delegate thanks to their constructor, and ArrayLists support remove(int index), so we're fine!
        so uhhh don't call this with a DefaultedList that doesn't have an ArrayList delegate, though I'm not sure why you would want to... */
        for (int i = 0; i < stacks.size(); i++){
            stacks.remove(i);
        }
        /* also this doesn't even remove from the DefaultedList itself - just its delegate. I don't think the two sync their sizes at all? that eventually causes
        absurdly long length problems in updateSlotStacks' parameters when those lists eventually get passed from here by Vanilla over networking into there - i just
        stinkily patched around it with that >= 63 check. The weirdest thing is, when you remove that check, there's not even an index out of bounds exception thrown. nothing in the console gives me any
        info on what's happening, but the setCursorStack and revision stuff never actually execute. i set breakpoints there to check and everything.
        my theory is that the loop takes so long to go through it times out whatever timer Minecraft has to make sure client tasks don't take too long and delay other things, so it's skipped over. 
        
        Theoretically, this means that if someone were to sit on an open screen and click between tabs hundreds of times without closing the screen, the game might crash from trying to send a packet
        too large, because it's containing a list that has 36*200 ish items - only the first 36 of which are non-ItemStack.EMPTY. I think. You could probably patch this issue for real with Mixins... */
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
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
            int heightMultiplier = (int)(i/9); // more slot coordinate calculation magic
            int placeMultiplier = i % 9;
            Slot slot = new Slot(INVENTORY, i, 8 + placeMultiplier*18, 23 + heightMultiplier*18);
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
