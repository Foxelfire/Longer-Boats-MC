package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EntityIdPayload(int entityID) implements CustomPayload {
    public static final CustomPayload.Id<EntityIdPayload> ID = new CustomPayload.Id<>(Identifier.of(LongerBoatsMod.MOD_ID, "entity_id_payload"));
    public static final PacketCodec<RegistryByteBuf, EntityIdPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityIdPayload::entityID,
            EntityIdPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
