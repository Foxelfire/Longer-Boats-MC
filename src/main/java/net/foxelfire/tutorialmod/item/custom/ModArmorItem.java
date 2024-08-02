package net.foxelfire.tutorialmod.item.custom;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.*;

public class ModArmorItem extends ArmorItem{

    private static final Map<ArmorMaterial, StatusEffectInstance> MATERIAL_TO_EFFECT_MAP = new HashMap<ArmorMaterial, StatusEffectInstance>();

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    // Doesn't work for other entities, but that's fine!
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(!world.isClient()){
            if(entity instanceof LivingEntity mob && correctArmorPower(mob) > -1 && correctArmorPower(mob) < 4){
                evaluateArmorEffects(mob, correctArmorPower(mob));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void evaluateArmorEffects(LivingEntity entity, int amplifier) {
        createEffectMap(amplifier);
        for (Map.Entry<ArmorMaterial, StatusEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            ArmorMaterial mapArmorMaterial = entry.getKey();
            StatusEffectInstance mapStatusEffect = entry.getValue();
            addStatusEffectForMaterial(entity, mapArmorMaterial, mapStatusEffect);
        }
    }
    private void addStatusEffectForMaterial(LivingEntity entity, ArmorMaterial mapArmorMaterial,
            StatusEffectInstance mapStatusEffect) {
        boolean alreadyHasEffect = entity.hasStatusEffect(mapStatusEffect.getEffectType());
        if(!alreadyHasEffect){
            entity.addStatusEffect(new StatusEffectInstance(mapStatusEffect));
        }       
    }

    private int correctArmorPower(LivingEntity entity){
        int numberOfPyriteArmorItems = -1;
        for(ItemStack equipmentSlot : entity.getArmorItems()){
            if(equipmentSlot.getItem() instanceof ModArmorItem){ // read: an instance of pyrite armor
                numberOfPyriteArmorItems++;
            }
        }
        return numberOfPyriteArmorItems;

    }
    private void createEffectMap(int amplifierValue){
        MATERIAL_TO_EFFECT_MAP.put(ModArmorMaterials.PYRITE, new StatusEffectInstance(StatusEffects.SPEED, 400, amplifierValue, false, false, false));
    }

}
