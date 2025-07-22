package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.items.armor.BlackDragonBoots;
import com.chen1335.legendaryRelics.items.armor.BlackDragonChestPlate;
import com.chen1335.legendaryRelics.items.armor.BlackDragonHelmet;
import com.chen1335.legendaryRelics.items.armor.BlackDragonLeggings;
import com.chen1335.legendaryRelics.items.curios.*;
import com.chen1335.legendaryRelics.items.misc.*;
import com.chen1335.legendaryRelics.items.weapons.WitheringBlade;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRItems {
    public static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(LegendaryRelics.MODID);

    public static final DeferredItem<SacredTalisman> SACRED_TALISMAN = ITEM_DEFERRED_REGISTER.register("sacred_talisman", SacredTalisman::new);

    public static final DeferredItem<AgglomerationMalice> AGGLOMERATION_MALICE = ITEM_DEFERRED_REGISTER.register("agglomeration_malice", AgglomerationMalice::new);

    public static final DeferredItem<ShieldRegenerator> SHIELD_REGENERATOR = ITEM_DEFERRED_REGISTER.register("shield_regenerator", ShieldRegenerator::new);

    public static final DeferredItem<HardenedRing> HARDENED_RING = ITEM_DEFERRED_REGISTER.register("hardened_ring", HardenedRing::new);

    public static final DeferredItem<DarkSteelClaw> DARK_STEEL_CLAW = ITEM_DEFERRED_REGISTER.register("dark_steel_claw", DarkSteelClaw::new);

    public static final DeferredItem<BlackDragonHelmet> BLACK_DRAGON_HELMET = ITEM_DEFERRED_REGISTER.register("black_dragon_helmet", BlackDragonHelmet::new);

    public static final DeferredItem<BlackDragonChestPlate> BLACK_DRAGON_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("black_dragon_chestplate", BlackDragonChestPlate::new);

    public static final DeferredItem<BlackDragonLeggings> BLACK_DRAGON_LEGGINGS = ITEM_DEFERRED_REGISTER.register("black_dragon_leggings", BlackDragonLeggings::new);

    public static final DeferredItem<BlackDragonBoots> BLACK_DRAGON_BOOTS = ITEM_DEFERRED_REGISTER.register("black_dragon_boots", BlackDragonBoots::new);

    public static final DeferredItem<DarkGoldForgingTool> DARK_GOLD_FORGING_TOOL = ITEM_DEFERRED_REGISTER.register("dark_gold_forging_tool", DarkGoldForgingTool::new);

    public static final DeferredItem<AncientFragment> ANCIENT_FRAGMENT = ITEM_DEFERRED_REGISTER.register("ancient_fragment", AncientFragment::new);

    public static final DeferredItem<TheOreCollectorsRing> THE_ORE_COLLECTORS_RING = ITEM_DEFERRED_REGISTER.register("the_ore_collectors_ring", TheOreCollectorsRing::new);

    public static final DeferredItem<LavaRing> LAVA_RING = ITEM_DEFERRED_REGISTER.register("lava_ring", LavaRing::new);

    public static final DeferredItem<NetherRing> NETHER_RING = ITEM_DEFERRED_REGISTER.register("nether_ring", NetherRing::new);

    public static final DeferredItem<NetherTalisman> NETHER_TALISMAN = ITEM_DEFERRED_REGISTER.register("nether_talisman", NetherTalisman::new);

    public static final DeferredItem<PurgatoryTalisman> PURGATORY_TALISMAN = ITEM_DEFERRED_REGISTER.register("purgatory_talisman", PurgatoryTalisman::new);

    public static final DeferredItem<HealingTalisman> HEALING_TALISMAN = ITEM_DEFERRED_REGISTER.register("healing_talisman", HealingTalisman::new);

    public static final DeferredItem<PerseveranceNecklace> PERSEVERANCE_NECKLACE = ITEM_DEFERRED_REGISTER.register("perseverance_necklace", PerseveranceNecklace::new);

    public static final DeferredItem<DragonScale> DRAGON_SCALE = ITEM_DEFERRED_REGISTER.register("dragon_scale", DragonScale::new);

    public static final DeferredItem<DarkGoldFragment> DARK_GOLD_FRAGMENT = ITEM_DEFERRED_REGISTER.register("dark_gold_fragment", DarkGoldFragment::new);

    public static final DeferredItem<DarkGold> DARK_GOLD = ITEM_DEFERRED_REGISTER.register("dark_gold", DarkGold::new);

    public static final DeferredItem<WitheringBlade> WITHERING_BLADE = ITEM_DEFERRED_REGISTER.register("withering_blade", WitheringBlade::new);

}
