package net.foxelfire.tutorialmod.screen;

import java.util.function.Predicate;

import net.foxelfire.tutorialmod.entity.custom.CedarBoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Box;

public class CedarBoatScreenHandler extends ScreenHandler{

    private Inventory inventory;
    public final CedarBoatEntity entity;
    private static Predicate<CedarBoatEntity> isPlayersEntity = new Predicate<CedarBoatEntity>() {
            @Override
            public boolean test(CedarBoatEntity entity) {
                return entity.hasPlayerRider();
            }
        };
            
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getEntitiesByClass(CedarBoatEntity.class, new Box(1.5, 0.4, 1.5, 1.5, 0.4, 1.5), isPlayersEntity).get(0));
    }
        
    public CedarBoatScreenHandler(int syncId, PlayerInventory inventory, CedarBoatEntity entity) {
        super(ModScreenHandlers.CEDAR_BOAT_SCREEN_HANDLER, syncId);
        checkSize((Inventory)entity, 4);
        this.inventory = (Inventory)entity;
        this.entity = entity;
        inventory.onOpen(inventory.player);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

}
