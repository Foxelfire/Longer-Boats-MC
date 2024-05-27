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
        @SuppressWarnings("unused") 
        /* why tf did i create an unused method to generate tags without generating any? 
        bc we will inevitably have to create corresponding tag variables 
        (public static final TagKey<Item> (or more likely, <Block>) EXAMPLE_TAG = createTag(example_json_filename)) 
        for any custom json tags we add to be recognizable for custom block/item classes 
        in the future with state.isIn(ModTags.Items or Blocks.EXAMPLE_TAG)
        This will get used at some point. And if not, I'm disappointed in you, future Foxel. DON'T FORGET YOU HAVE THIS METHOD.
        */
        private static TagKey<Item> createTag(String name){
            return TagKey.of(RegistryKeys.ITEM, new Identifier(TutorialMod.MOD_ID, name));
        }
    }
}
