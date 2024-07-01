package net.foxelfire.tutorialmod.util;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.foxelfire.tutorialmod.block.ModBlocks;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.villager.ModVillagers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

public class ModCustomTrades {
    public static void registerCustomTrades(){
        addLevel1Trades();
        addLevel2Trades();
        addLevel3Trades();
        addLevel4Trades();
        addLevel5Trades();
    }
    // TODO: Switch everything back to direct TradeOffer() calls? Probably won't fix the post-exit crash but worth a shot
    // if that doesn't work, either figure out how tf to update lwjgl or give up
    // UPDATE: this crash is probably mojang's fault! 1.20.1 has a broken lwjgl version that can fuck with mods. this in theory should solve itself when i update my mod version
    public static void addLevel1Trades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories -> {
            factories.add((entity, random) -> new TradeOffers.BuyForOneEmeraldFactory(ModItems.PYRITE, 4, 16, 1).create(entity, random));
            /* STOP DOING WHATEVER I JUST FIGURED OUT HOW TO DO
            * "Yeah give me static classes that i can instantiate anyways bc why not" - Statements dreamed up by the utterly deranged
            * "Hello I would like new Tree.Apple(4).makeStack() please"
            * THEY HAVE PLAYED US FOR ABSOLUTE FOOLS
             */
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories ->{
            factories.add((entity, random) -> new TradeOffers.BuyForOneEmeraldFactory(Items.RAW_COPPER, 16, 16, 1).create(entity, random));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories ->{
            factories.add((entity, random) -> new TradeOffers.BuyForOneEmeraldFactory(Items.COAL, 12, 12, 1).create(entity, random));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.STONE_PICKAXE, 1), new ItemStack(Items.EMERALD, 1), 3, 2, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.COBBLESTONE, 32), 12, 2, 0.05f));
        });
    }

    public static void addLevel2Trades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 2, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.BUCKET, 1), new ItemStack(Items.EMERALD, 2), 3, 10, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 2, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.TORCH, 32), new ItemStack(Items.EMERALD, 2), 8, 10, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 2, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.IRON_INGOT, 1), 12, 5, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 2, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.COOKED_CHICKEN, 16), new ItemStack(Items.EMERALD, 1), 8, 10, 0.05f));
        });
    }

    public static void addLevel3Trades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 3, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(Items.COPPER_BLOCK, 1), 16, 10, 0.07f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 3, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(ModBlocks.PYRITE_BLOCK, 1), 16, 10, 0.07f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 3, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.COAL_BLOCK, 1), 16, 10, 0.07f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 3, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.IRON_PICKAXE, 1), new ItemStack(Items.EMERALD, 2), 3, 20, 0.05f));
        });
    }
    public static void addLevel4Trades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 4, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.COOKED_BEEF, 16), new ItemStack(Items.EMERALD, 1), 12, 30, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 4, factories ->{
            factories.add((entity, random) -> new TradeOffers.SellEnchantedToolFactory(Items.IRON_PICKAXE, 1, 3, 15).create(entity, random));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 4, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.COBBLED_DEEPSLATE, 64), 12, 15, 0.05f));
        });
    }
    public static void addLevel5Trades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 5, factories ->{
            factories.add((entity, random) -> new TradeOffers.SellEnchantedToolFactory(Items.DIAMOND_PICKAXE, 1, 3, 15).create(entity, random));
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 5, factories ->{
            factories.add((entity, random) -> new TradeOffers.SellEnchantedToolFactory(ModItems.PYRITE_PICKAXE, 1, 3, 15).create(entity, random));
        });
    }
}
