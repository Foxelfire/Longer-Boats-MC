package net.foxelfire.tutorialmod.util;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.villager.ModVillagers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

public class ModCustomTrades {
    public static void registerCustomTrades(){
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories -> {
            factories.add((entity, random) -> new TradeOffers.BuyForOneEmeraldFactory(ModItems.PYRITE, 4, 16, 1).create(entity, random));
            /* STOP DOING WHATEVER I JUST FIGURED OUT HOW TO DO
            * "Yeah give me static classes that i can instantiate anyways bc why not" - Statements dreamed up by the utterly deranged
            * "Hello I would like new Tree.Apple(4).makeStack() please"
            * THEY HAVE PLAYED US FOR ABSOLUTE FOOLS
             */
        });
        TradeOfferHelper.registerVillagerOffers(ModVillagers.MINER_PROFESSION, 1, factories ->{
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.COPPER_ORE, 12), new ItemStack(Items.EMERALD, 1), 10, 3, 0.075f));
        });
    }
}
