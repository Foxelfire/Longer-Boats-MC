package net.foxelfire.tutorialmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class CedarBoatScreen extends HandledScreen<CedarBoatScreen.CedarBoatScreenHandler>{
    public NewTabWidget previous;
    public NewTabWidget next;
    public DefaultedList<ItemStack> tabInventory;
    static final SimpleInventory INVENTORY = new SimpleInventory(27);
    private static int currentTab = 0;
    private static final Identifier TEXTURE = new Identifier(TutorialMod.MOD_ID, "textures/gui/boat_tab.png");

    public CedarBoatScreen(CedarBoatScreen.CedarBoatScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
        titleX = 4;
        titleY = -12;
        playerInventoryTitleX-=4;
        playerInventoryTitleY+=6;
        backgroundHeight+=32;
        int x = (width - backgroundWidth) / 2 + 55;
        int y = (height - backgroundHeight)/2 + 16;
        previous = NewTabWidget.builder(Text.literal("Previous Tab"), true, button -> {
            handler.switchTab(currentTab - 1);
            TutorialMod.LOGGER.info("Previous Tab Button was pressed!");
        })
        .dimensions(x, y, 24, 24)
        .tooltip(Tooltip.of(Text.literal("Previous Tab Button")))
        .build();
        next = NewTabWidget.builder(Text.literal("Next Tab"), false, button -> {
            handler.switchTab(currentTab + 1);
            TutorialMod.LOGGER.info("Next Tab Button was pressed!");
        })
        .dimensions((int)(x*1.25), y, 24, 24)
        .tooltip(Tooltip.of(Text.literal("Next Tab Button")))
        .build();
        addDrawableChild(previous);
        addDrawableChild(next);
    }

    protected static void saveEntityInventory(CedarBoatScreenHandler handler, int tab){
        DefaultedList<ItemStack> previousStacks = INVENTORY.stacks;
        handler.entity.sendC2SInventoryPacket(previousStacks, tab);
    }
    
    protected static void manageEntityInventory(int tab, CedarBoatScreenHandler handler){
        /* Okay, so when you add a slot, the slot does something strange (caching or smth along those lines) to the index of the inventory the slot is linked to
         * that locks that index into never being able to change. This causes it to never update what item is in that index of the inventory
         * unless it's been removed. So in order to make slots reflect the new inventory contents if a button is pressed to move to a new section of
         * the inventory, you have to delete all the current stale slots and make new ones. You can do this by clearing the list of slots (even though said
         * list is final) and re-adding slots to it. The vanilla creative inventory does the exact same thing to handle its tab buttons, so I copied and adapted that
         * logic to fit here.
        */
        TutorialMod.LOGGER.info("Pulling from tab: " + tab);
        DefaultedList<ItemStack> inventoryStacks = handler.entity.getInventoryTabAt(tab);
        handler.itemList = inventoryStacks;
        handler.slots.clear();
        handler.addPlayerInventory(handler.playerInventory);
        for(int i = 0; i < 27; i++){
            int heightMultiplier = (int)(i/9);
            int xMultiplier = i % 9;
            Slot slot = new Slot(INVENTORY, i, 8 + xMultiplier * 18, 23 + heightMultiplier*18);
            handler.addSlotPublicWrapper(slot);
        }
        handler.addDisplayArea();
        currentTab = tab;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram); // normal shader menu stuff, apparently
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE); // breaking news: menus are shaders
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight)/2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight); // u and v (the 2 zeroes) are offsets
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, (currentTab+1) + " / " + handler.entity.getNumberOfChests(), (int)(((width - backgroundWidth) / 2 + 50)*1.125), (height - backgroundHeight)/2 + 22, 0x303030, false);
        drawMouseoverTooltip(context, mouseX, mouseY);
    } 

    public static class CedarBoatScreenHandler extends ScreenHandler {

        public final CedarBoatEntity entity;
        public final PlayerEntity player;
        public final PlayerInventory playerInventory;
        public DefaultedList<ItemStack> itemList = DefaultedList.of();
        

        public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
            this(syncId, inventory, inventory.player.getVehicle());
        }
            
        public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, Entity entity) {
            super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
            this.entity = (CedarBoatEntity)entity;
            this.player = inventory.player;
            this.playerInventory = inventory;
            manageEntityInventory(0, this);
            inventory.onOpen(inventory.player);
        }

        public void addSlotPublicWrapper(Slot slot){
            this.addSlot(slot);
        }

        public void switchTab(int tabIndex){
            if(tabIndex >= entity.getNumberOfChests() || tabIndex < 0){
                TutorialMod.LOGGER.info("Tab number too big/small! Only good tabs are: 0, 1, 2 and your tab is: " + tabIndex);
                TutorialMod.LOGGER.info("Entity's chest number: " + entity.getNumberOfChests());
                return;
            }
            saveEntityInventory(this, currentTab);
            manageEntityInventory(tabIndex, this);
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

        private void addDisplayArea(){
            for(int i = 0; i < 27; i++){
                INVENTORY.setStack(i, this.itemList.get(i));
            }
        }

        @Override
        public void onClosed(PlayerEntity player){
            saveEntityInventory(this, currentTab);
            manageEntityInventory(0, this);
            super.onClosed(player);
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

    }
}