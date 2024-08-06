package net.foxelfire.tutorialmod.recipe;

import net.foxelfire.tutorialmod.TutorialMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void registerRecipes(){
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(TutorialMod.MOD_ID, ElementExtractorRecipe.Serializer.ID),
        ElementExtractorRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(TutorialMod.MOD_ID, ElementExtractorRecipe.Type.ID),
        ElementExtractorRecipe.Type.INSTANCE);
        // yes, it needs two. A thing to tell MC what the recipe is, and a thing to pass it from client to server.
    }
}
