package com.chen1335.legendaryRelics.common.lootModifier;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public class LootEntries {

    public static LootEntry SKELETON_THROWING_KNIFE = LootEntry.ofSimpleItem(0.3, List.of(
            BuiltInLootTables.DESERT_PYRAMID.location()
    ), LRItems.SKELETON_THROWING_KNIFE);

    public static LootEntry HEALING_TALISMAN = LootEntry.ofSimpleItem(0.2, List.of(
            BuiltInLootTables.JUNGLE_TEMPLE.location(),
            BuiltInLootTables.VILLAGE_WEAPONSMITH.location(),
            BuiltInLootTables.VILLAGE_TEMPLE.location(),
            BuiltInLootTables.VILLAGE_ARMORER.location()
    ), LRItems.HEALING_TALISMAN);

    public static LootEntry NETHER_TALISMAN = LootEntry.ofSimpleItem(0.25, List.of(
            BuiltInLootTables.BASTION_TREASURE.location()
    ), LRItems.NETHER_TALISMAN);

    public static LootEntry NETHER_RING = LootEntry.ofSimpleItem(0.25, List.of(
            BuiltInLootTables.NETHER_BRIDGE.location(),
            BuiltInLootTables.BASTION_OTHER.location(),
            BuiltInLootTables.BASTION_BRIDGE.location(),
            BuiltInLootTables.BASTION_HOGLIN_STABLE.location(),
            BuiltInLootTables.BASTION_TREASURE.location()
    ), LRItems.NETHER_RING);

    public static LootEntry LAVA_RING = LootEntry.ofSimpleItem(0.5, List.of(
            BuiltInLootTables.RUINED_PORTAL.location()
    ), LRItems.LAVA_RING);

    public static LootEntry THE_ORE_COLLECTORS_RING = LootEntry.ofSimpleItem(0.25, List.of(
            BuiltInLootTables.ABANDONED_MINESHAFT.location()
    ), LRItems.THE_ORE_COLLECTORS_RING);

    public static LootEntry DARK_STEEL_CLAW = LootEntry.ofSimpleItem(0.25, List.of(
            BuiltInLootTables.ANCIENT_CITY.location(),
            BuiltInLootTables.ANCIENT_CITY_ICE_BOX.location()
    ), LRItems.DARK_STEEL_CLAW);

    public static LootEntry SACRED_TALISMAN = LootEntry.ofSimpleItem((double) 1 / 3, List.of(
            BuiltInLootTables.VILLAGE_WEAPONSMITH.location()
    ), LRItems.SACRED_TALISMAN);

    public static LootEntry AGGLOMERATION_MALICE = LootEntry.ofSimpleItem(0.5, List.of(
            BuiltInLootTables.SIMPLE_DUNGEON.location()
    ), LRItems.AGGLOMERATION_MALICE);

    public static LootEntry HARDENED_RING = LootEntry.ofSimpleItem(0.25, List.of(
            BuiltInLootTables.DESERT_PYRAMID.location()
    ), LRItems.HARDENED_RING);

    public static LootEntry ANCIENT_FRAGMENT_HIGH = LootEntry.ofSpecialLoot("ancient_fragment_high", 0.4, List.of(
            BuiltInLootTables.ANCIENT_CITY.location(),
            BuiltInLootTables.ANCIENT_CITY_ICE_BOX.location(),
            BuiltInLootTables.BURIED_TREASURE.location(),
            BuiltInLootTables.SIMPLE_DUNGEON.location(),
            BuiltInLootTables.END_CITY_TREASURE.location(),
            BuiltInLootTables.BASTION_TREASURE.location()
    ), LRItems.ANCIENT_FRAGMENT);

    public static LootEntry ANCIENT_FRAGMENT_NORMAL = LootEntry.ofSpecialLoot("ancient_fragment_normal", 0.2, List.of(
            BuiltInLootTables.DESERT_PYRAMID.location(),
            BuiltInLootTables.ABANDONED_MINESHAFT.location(),
            BuiltInLootTables.STRONGHOLD_LIBRARY.location(),
            BuiltInLootTables.STRONGHOLD_CORRIDOR.location(),
            BuiltInLootTables.STRONGHOLD_CROSSING.location(),
            BuiltInLootTables.RUINED_PORTAL.location(),
            BuiltInLootTables.NETHER_BRIDGE.location(),
            BuiltInLootTables.BASTION_OTHER.location(),
            BuiltInLootTables.BASTION_BRIDGE.location(),
            BuiltInLootTables.BASTION_HOGLIN_STABLE.location()
    ), LRItems.ANCIENT_FRAGMENT);

    public static LootEntry DARK_GOLD_FRAGMENT_HIGH = LootEntry.ofSpecialLoot("dark_gold_fragment_high", 0.4, List.of(
            BuiltInLootTables.BASTION_TREASURE.location()
    ), LRItems.DARK_GOLD_FRAGMENT);

    public static LootEntry DARK_GOLD_FRAGMENT_NORMAL = LootEntry.ofSpecialLoot("dark_gold_fragment_normal", 0.2, List.of(
            BuiltInLootTables.NETHER_BRIDGE.location(),
            BuiltInLootTables.BASTION_OTHER.location(),
            BuiltInLootTables.BASTION_BRIDGE.location(),
            BuiltInLootTables.BASTION_HOGLIN_STABLE.location(),
            BuiltInLootTables.BASTION_TREASURE.location()
    ), LRItems.DARK_GOLD_FRAGMENT);

    public static void init() {
    }
}
