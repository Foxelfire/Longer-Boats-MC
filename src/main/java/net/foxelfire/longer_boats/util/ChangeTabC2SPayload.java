package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChangeTabC2SPayload(int entityId, int tab) implements CustomPayload {
    public static final CustomPayload.Id<ChangeTabC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(LongerBoatsMod.MOD_ID, "change_tab_payload"));
    public static final PacketCodec<RegistryByteBuf, ChangeTabC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ChangeTabC2SPayload::entityId,
            PacketCodecs.INTEGER, ChangeTabC2SPayload::tab,
            ChangeTabC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
