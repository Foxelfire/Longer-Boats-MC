package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.entity.custom.AbstractLongBoatEntity;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class TabSlot extends Slot {
    private final AbstractLongBoatEntity entity;
    private final LongBoatScreenHandler handler;
    private final int slotIndex;

    public TabSlot(AbstractLongBoatEntity entity, LongBoatScreenHandler handler, int index, Inventory dummy, int x, int y) {
        super(dummy, index, x, y);
        this.entity = entity;
        this.handler = handler;
        this.slotIndex = index;
    }

    @Override
    public ItemStack getStack() {
        return entity.getTab(handler.getCurrentTab()).get(slotIndex);
    }

    @Override
    public void setStack(ItemStack stack) {
        entity.getTab(handler.getCurrentTab()).set(slotIndex, stack);
        this.markDirty();
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack){
        entity.getTab(handler.getCurrentTab()).set(slotIndex, stack);
    }

    @Override
    public void markDirty() {
        entity.inventoryDirty(true);
    }

    @Override
    public ItemStack takeStack(int amount){
        ItemStack stack = getStack();
        ItemStack removed = stack.split(amount);

        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }
}