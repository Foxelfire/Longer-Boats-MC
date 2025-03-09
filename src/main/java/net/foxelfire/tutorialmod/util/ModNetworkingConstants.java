package net.foxelfire.tutorialmod.util;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.util.Identifier;

public class ModNetworkingConstants {
    public static final Identifier INVENTORY_S2C_SYNCING_PACKET_ID = Identifier.of(TutorialMod.MOD_ID, "inventory_packet_s2c");
    public static final Identifier INVENTORY_C2S_SYNCING_PACKET_ID = Identifier.of(TutorialMod.MOD_ID, "inventory_packet_c2s");
}
