package net.foxelfire.tutorialmod.util;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        // this is where you'd add the tag creator for block tags - if you had any planned!
    }
    public static class Items {

        // I used the method!! ...but only to generate a random crafting recipe in ModRecipeProvider
        public static final TagKey<Item> KONPEITO_SEED_CORES = createTag("konpeito_seed_cores");
        private static TagKey<Item> createTag(String name){
            return TagKey.of(RegistryKeys.ITEM, new Identifier(TutorialMod.MOD_ID, name));
        }
    }
}
