package net.foxelfire.tutorialmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.block.custom.DewfruitCropBlock;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

public class ModLootTableProvider extends FabricBlockLootTableProvider{

    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        // the condition for dropping dewfruit (must be at full age)
        BlockStatePropertyLootCondition.Builder dewfruitPredicateBuilder = BlockStatePropertyLootCondition.builder(ModBlocks.DEWFRUIT_CROP)
        .properties(StatePredicate.Builder.create().exactMatch(DewfruitCropBlock.AGE, 15));

        addDrop(ModBlocks.LIGHT_CRYSTAL_BLOCK);
        addDrop(ModBlocks.LIGHT_LANTERN_BLOCK, lightLanternDrops(ModBlocks.LIGHT_LANTERN_BLOCK));
        addDrop(ModBlocks.PYRITE_BLOCK);
        addDrop(ModBlocks.PYRITE_DOOR, doorDrops(ModBlocks.PYRITE_DOOR));
        addDrop(ModBlocks.PYRITE_STAIRS);
        addDrop(ModBlocks.PYRITE_SLAB, slabDrops(ModBlocks.PYRITE_SLAB));
        addDrop(ModBlocks.PYRITE_PRESSURE_PLATE);
        addDrop(ModBlocks.PYRITE_BUTTON);
        addDrop(ModBlocks.PYRITE_WALL);
        addDrop(ModBlocks.PYRITE_TRAPDOOR);

        addDrop(ModBlocks.CEDAR_PLANKS);
        addDrop(ModBlocks.CEDAR_LOG);
        addDrop(ModBlocks.CEDAR_WOOD);
        addDrop(ModBlocks.STRIPPED_CEDAR_LOG);
        addDrop(ModBlocks.STRIPPED_CEDAR_WOOD);

        addDrop(ModBlocks.CEDAR_LEAVES, leavesDrops(ModBlocks.CEDAR_LEAVES, ModBlocks.CEDAR_LEAVES, BlockLootTableGenerator.SAPLING_DROP_CHANCE));
        addDrop(ModBlocks.DEWFRUIT_CROP, cropDrops(ModBlocks.DEWFRUIT_CROP, ModItems.DEWFRUIT, ModItems.DEWFRUIT_SEEDS, dewfruitPredicateBuilder));
        addDrop(ModBlocks.CLOVER_FLOWER);
        addDrop(ModBlocks.CLOVER);
        addPottedPlantDrops(ModBlocks.POTTED_CLOVER);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // ik this works bc mc does it
    public LootTable.Builder lightLanternDrops(Block drop) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop, (LootPoolEntry.Builder)this.applyExplosionDecay(drop, ((LeafEntry.Builder)ItemEntry.builder(ModItems.LIGHT_SHARD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 6.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))));
    }
}
