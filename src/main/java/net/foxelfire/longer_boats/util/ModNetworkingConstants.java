package net.foxelfire.longer_boats.util;

import net.foxelfire.longer_boats.LongerBoatsMod;
import net.minecraft.util.Identifier;

public class ModNetworkingConstants {
    public static final Identifier INVENTORY_S2C_SYNCING_PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "inventory_packet_s2c");
    public static final Identifier INVENTORY_C2S_SYNCING_PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "inventory_packet_c2s");
    public static final Identifier TOTAL_MOVEMENT_INPUTS_S2C_PACKET_ID = Identifier.of(LongerBoatsMod.MOD_ID, "movement_packet_s2c");
}
