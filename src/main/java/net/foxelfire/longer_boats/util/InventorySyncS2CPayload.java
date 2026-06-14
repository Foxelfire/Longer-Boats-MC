package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record InventorySyncS2CPayload(ArrayList<ItemStack> inventory, int entityId, int nextTab) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "inventory_packet_s2c");
    public static final Id<InventorySyncS2CPayload> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, InventorySyncS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_PACKET_CODEC), InventorySyncS2CPayload::inventory,
            PacketCodecs.INTEGER, InventorySyncS2CPayload::entityId,
            PacketCodecs.INTEGER, InventorySyncS2CPayload::nextTab,
            InventorySyncS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
