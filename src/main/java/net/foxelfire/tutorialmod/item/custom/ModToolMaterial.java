package net.foxelfire.tutorialmod.item.custom;

import java.util.function.Supplier;

import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum ModToolMaterial implements ToolMaterial{
    PYRITE(1, 150, 11.0f, 1.0f, 20, () -> Ingredient.ofItems(ModItems.PYRITE_INGOT));
    // you can just add another enum here if u want another tool material (but tools of vanilla classes are boring!)
    private final int miningLevel;
    private final int miningDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterial(int miningLevel, int miningDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient){
        this.miningLevel = miningLevel;
        this.miningDurability = miningDurability;
        this.miningSpeed = miningSpeed + 4.0f; // minecraft is wack af, all tools have this weird -4.0 slowdown applied to them
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }


    @Override
    public int getDurability() {
        return this.miningDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

}
