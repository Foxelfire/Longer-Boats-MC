package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record MovementInputC2SPayload(ArrayList<ItemStack> inventory, int entityId, float forwardSpeed, float sidewaysSpeed) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "movement_packet_s2c");
    public static final CustomPayload.Id<MovementInputC2SPayload> ID = new CustomPayload.Id<>(PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, MovementInputC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_PACKET_CODEC), MovementInputC2SPayload::inventory,
            PacketCodecs.INTEGER, MovementInputC2SPayload::entityId,
            PacketCodecs.FLOAT, MovementInputC2SPayload::forwardSpeed,
            PacketCodecs.FLOAT, MovementInputC2SPayload::sidewaysSpeed,
            MovementInputC2SPayload::new
    );

    @Override
    public CustomPayload.Id<MovementInputC2SPayload> getId() {
        return ID;
    }
}
