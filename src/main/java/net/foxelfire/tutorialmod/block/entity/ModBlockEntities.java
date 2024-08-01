package net.foxelfire.tutorialmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    
    public static final BlockEntityType<ElementExtractorBlockEntity> ELEMENT_EXTRACTOR_BLOCK_ENTITY =
    Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TutorialMod.MOD_ID, "element_extractor_be"),
    FabricBlockEntityTypeBuilder.create(ElementExtractorBlockEntity::new, ModBlocks.ELEMENT_EXTRACTOR_BLOCK).build());

    public static void registerBlockEntities(){
        TutorialMod.LOGGER.info("Registering Block Entities for: " + TutorialMod.MOD_ID);
    }
}
