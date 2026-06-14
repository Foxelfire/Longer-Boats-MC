package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record MovementInputS2CPayload(int entityId, float forwardSpeed, float sidewaysSpeed) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "movement_packet_s2c");
    public static final CustomPayload.Id<MovementInputS2CPayload> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, MovementInputS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, MovementInputS2CPayload::entityId,
            PacketCodecs.FLOAT, MovementInputS2CPayload::forwardSpeed,
            PacketCodecs.FLOAT, MovementInputS2CPayload::sidewaysSpeed,
            MovementInputS2CPayload::new
    );

    @Override
    public CustomPayload.Id<MovementInputS2CPayload> getId() {
        return ID;
    }
}
