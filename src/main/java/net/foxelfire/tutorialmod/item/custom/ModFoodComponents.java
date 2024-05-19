package net.foxelfire.tutorialmod.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200), 1f).build();
    public static final FoodComponent BLACK_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200), 1f).build();
    public static final FoodComponent BLUE_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200), 1f).build();
    public static final FoodComponent BROWN_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20), 1f).build();
    public static final FoodComponent CYAN_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 200), 1f).build();
    public static final FoodComponent GRAY_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1), 1f).build();
    public static final FoodComponent GREEN_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 2000), 1f).build();
    public static final FoodComponent LIGHT_BLUE_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1), 1f).build();
    public static final FoodComponent LIGHT_GRAY_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2000), 1f).build();
    public static final FoodComponent LIME_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 200), 1f).build();
    public static final FoodComponent MAGENTA_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 200, 1), 1f).build();
    public static final FoodComponent ORANGE_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200), 1f).build();
    public static final FoodComponent PINK_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200), 1f).build();
    public static final FoodComponent PURPLE_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 1), 1f).build();
    public static final FoodComponent RED_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1), 1f).build();
    public static final FoodComponent YELLOW_KONPEITO = new FoodComponent.Builder().hunger(1).snack().saturationModifier(.1f).statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200, 2), 1f).build();
}
