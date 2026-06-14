package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record InventorySyncC2SPayload(ArrayList<ItemStack> inventory, int entityId, int prevTab, int tab) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "inventory_packet_c2s");
    public static final Id<InventorySyncC2SPayload> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, InventorySyncC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_PACKET_CODEC), InventorySyncC2SPayload::inventory,
            PacketCodecs.INTEGER, InventorySyncC2SPayload::entityId,
            PacketCodecs.INTEGER, InventorySyncC2SPayload::prevTab,
            PacketCodecs.INTEGER, InventorySyncC2SPayload::tab,
            InventorySyncC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
