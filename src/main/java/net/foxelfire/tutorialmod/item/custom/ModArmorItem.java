package net.foxelfire.tutorialmod.item.custom;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.PlayerEntity;

public class ModArmorItem extends ArmorItem{

    private static final Map<ArmorMaterial, StatusEffectInstance> MATERIAL_TO_EFFECT_MAP = (new ImmutableMap.Builder<ArmorMaterial, StatusEffectInstance>()
    .put(ModArmorMaterials.PYRITE, new StatusEffectInstance(StatusEffects.SPEED, 400, 0, false, false, false))).build();

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    // TODO maybe: make it so any moving entity (mob or player, but not boat) can wear one piece of armor to get the effect, and the effect gets better the more pieces you have
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(!world.isClient()){
            if(entity instanceof PlayerEntity player && hasFullSuitOfArmorOn(player)){
                evaluateArmorEffects(player);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void evaluateArmorEffects(PlayerEntity player) {
        for (Map.Entry<ArmorMaterial, StatusEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            ArmorMaterial mapArmorMaterial = entry.getKey();
            StatusEffectInstance mapStatusEffect = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)){
                addStatusEffectForMaterial(player, mapArmorMaterial, mapStatusEffect);
            }
        }
    }
    private void addStatusEffectForMaterial(PlayerEntity player, ArmorMaterial mapArmorMaterial,
            StatusEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasStatusEffect(mapStatusEffect.getEffectType());
        if(hasCorrectArmorOn(mapArmorMaterial, player) && !hasPlayerEffect){
            player.addStatusEffect(new StatusEffectInstance(mapStatusEffect));
        }       
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, PlayerEntity player) {
        for (ItemStack slot : player.getInventory().armor) {
            if(!(slot.getItem() instanceof ArmorItem)){
                return false;
            }
        }
        ArmorItem boots = (ArmorItem)player.getInventory().getArmorStack(0).getItem();
        ArmorItem leggings = (ArmorItem)player.getInventory().getArmorStack(1).getItem();
        ArmorItem chestplate = (ArmorItem)player.getInventory().getArmorStack(2).getItem();
        ArmorItem helmet = (ArmorItem)player.getInventory().getArmorStack(3).getItem();

        return helmet.getMaterial() == material && leggings.getMaterial() == material && boots.getMaterial() == material && chestplate.getMaterial() == material;
    }

    private boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack chestplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);
        return !helmet.isEmpty() && !chestplate.isEmpty() && !leggings.isEmpty() && !boots.isEmpty();
    }

}
