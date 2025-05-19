package com.chen1335.legendaryRelics.data.lootTables;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.data.DataMain;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class LRGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public LRGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {
        simpleAdd(BuiltInLootTables.VILLAGE_WEAPONSMITH.location());
        simpleAdd(BuiltInLootTables.SIMPLE_DUNGEON.location());
        simpleAdd(BuiltInLootTables.DESERT_PYRAMID.location());
        addModifiers("ancient_fragment_add", LRLootTableProvider.LootTableModifier.ANCIENT_FRAGMENT_ADD.location(),
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
        );

        addModifiers("ancient_fragment_add_high_chance", LRLootTableProvider.LootTableModifier.ANCIENT_FRAGMENT_ADD_HIGH_CHANCE.location(),
                BuiltInLootTables.ANCIENT_CITY.location(),
                BuiltInLootTables.ANCIENT_CITY_ICE_BOX.location(),
                BuiltInLootTables.BURIED_TREASURE.location(),
                BuiltInLootTables.SIMPLE_DUNGEON.location(),
                BuiltInLootTables.END_CITY_TREASURE.location(),
                BuiltInLootTables.BASTION_TREASURE.location()
        );
    }

    private void simpleAdd(ResourceLocation resourceLocation) {
        add(resourceLocation.toString().replace(":", "_"), new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(resourceLocation).build()}, ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(DataMain.modid(), "modifier/" + resourceLocation.getPath()))));
    }

    private void addModifiers(String name, ResourceLocation resourceLocation, ResourceLocation... target) {
        for (ResourceLocation location : target) {
            add(location.toString().replace(":", "_") + "_" + name, new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(location).build()}, ResourceKey.create(Registries.LOOT_TABLE, resourceLocation)));
        }
    }

    private static ResourceKey<LootTable> key(String pName) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, pName));
    }
}
