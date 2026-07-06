package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InventorySizeS2CPayload(int entityId, int chestCount) implements CustomPayload {
    public static final CustomPayload.Id<InventorySizeS2CPayload> ID = new CustomPayload.Id<>(Identifier.of(LongerBoatsMod.MOD_ID, "inventory_size_payload"));
    public static final PacketCodec<RegistryByteBuf, InventorySizeS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, InventorySizeS2CPayload::entityId,
            PacketCodecs.INTEGER, InventorySizeS2CPayload::chestCount,
            InventorySizeS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
