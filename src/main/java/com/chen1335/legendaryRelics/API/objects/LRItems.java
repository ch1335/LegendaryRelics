package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.items.curios.*;
import com.chen1335.legendaryRelics.items.misc.AncientFragment;
import com.chen1335.legendaryRelics.items.misc.DarkGoldForgingTool;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRItems {
    public static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(LegendaryRelics.MODID);

    public static final DeferredItem<SacredTalisman> SACRED_TALISMAN = ITEM_DEFERRED_REGISTER.register("sacred_talisman", SacredTalisman::new);

    public static final DeferredItem<AgglomerationMalice> AGGLOMERATION_MALICE = ITEM_DEFERRED_REGISTER.register("agglomeration_malice", AgglomerationMalice::new);

    public static final DeferredItem<ShieldRegenerator> SHIELD_REGENERATOR = ITEM_DEFERRED_REGISTER.register("shield_regenerator", ShieldRegenerator::new);

    public static final DeferredItem<HardenedRing> HARDENED_RING = ITEM_DEFERRED_REGISTER.register("hardened_ring", HardenedRing::new);

    public static final DeferredItem<DarkSteelClaw> DARK_STEEL_CLAW = ITEM_DEFERRED_REGISTER.register("dark_steel_claw", DarkSteelClaw::new);


    public static final DeferredItem<DarkGoldForgingTool> DARK_GOLD_FORGING_TOOL = ITEM_DEFERRED_REGISTER.register("dark_gold_forging_tool", DarkGoldForgingTool::new);

    public static final DeferredItem<AncientFragment> ANCIENT_FRAGMENT = ITEM_DEFERRED_REGISTER.register("ancient_fragment", AncientFragment::new);

}
