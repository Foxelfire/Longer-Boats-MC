package net.foxelfire.tutorialmod.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {
    private static final Identifier JUNGLE_CHEST_ID = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier PYRAMID_CHEST_ID = new Identifier("minecraft", "chests/desert_pyramid");


    public static void modifyLootTables(){
        TutorialMod.LOGGER.info("Modifying Vanilla Loot Tables for " + TutorialMod.MOD_ID);
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
                if(JUNGLE_CHEST_ID.equals(id)){
                    LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(0.5f))
                    .with(ItemEntry.builder(ModItems.IRON_DETECTOR))
                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                    tableBuilder.pool(poolBuilder.build());
                }
        });

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if(PYRAMID_CHEST_ID.equals(id)){
                LootPool.Builder poolBuilder = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .conditionally(RandomChanceLootCondition.builder(0.2f))
                .with(ItemEntry.builder(ModItems.GOLD_DETECTOR))
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
    });
    }
}
