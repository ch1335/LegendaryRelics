package com.chen1335.legendaryRelics.data.lootTables;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.data.DataMain;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LRLootTableProvider extends LootTableProvider {
    public LRLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableModifier::new, LootContextParamSets.CHEST)
        ), registries);
    }

    public static class LootTableModifier implements LootTableSubProvider {
        private final HolderLookup.Provider provider;

        public static final ResourceKey<LootTable> VILLAGE_WEAPONSMITH_MODIFIER = registerModifier(BuiltInLootTables.VILLAGE_WEAPONSMITH.location());

        public static final ResourceKey<LootTable> SIMPLE_DUNGEON_MODIFIER = registerModifier(BuiltInLootTables.SIMPLE_DUNGEON.location());

        public static final ResourceKey<LootTable> DESERT_PYRAMID_MODIFIER = registerModifier(BuiltInLootTables.DESERT_PYRAMID.location());

        public static final ResourceKey<LootTable> ANCIENT_FRAGMENT_ADD = registerModifier("chests/ancient_fragment_add");

        public static final ResourceKey<LootTable> ANCIENT_FRAGMENT_ADD_HIGH_CHANCE = registerModifier("chests/ancient_fragment_add_high_chance");

        public static final ResourceKey<LootTable> DARK_STEEL_CLAW = registerModifier("chests/dark_steel_claw");

        public static final ResourceKey<LootTable> THE_ORE_COLLECTORS_RING = registerModifier("chests/the_ore_collectors_ring");

        public static final ResourceKey<LootTable> LAVA_RING = registerModifier("chests/lava_ring");

        public static final ResourceKey<LootTable> NETHER_RING = registerModifier("chests/nether_ring");

        public static final ResourceKey<LootTable> NETHER_TALISMAN = registerModifier("chests/nether_talisman");

        public static final ResourceKey<LootTable> HEALING_TALISMAN = registerModifier("chests/healing_talisman");

        public static final ResourceKey<LootTable> DRAGON_SCALE = registerModifier("entities/healing_talisman");

        public static final ResourceKey<LootTable> DARK_GOLD_FRAGMENTS = registerModifier("chests/dark_gold_fragments");

        public static final ResourceKey<LootTable> DARK_GOLD_FRAGMENTS_HIGH_CHANCE = registerModifier("chests/dark_gold_fragments_high_chance");

        public LootTableModifier(HolderLookup.Provider provider) {
            this.provider = provider;
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {

            output.accept(DARK_GOLD_FRAGMENTS_HIGH_CHANCE,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(40))
                                    .add(LootItem.lootTableItem(LRItems.DARK_GOLD_FRAGMENT).setWeight(10))

                    )
            );

            output.accept(DARK_GOLD_FRAGMENTS,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(90))
                                    .add(LootItem.lootTableItem(LRItems.DARK_GOLD_FRAGMENT).setWeight(10))

                    )
            );

            output.accept(DRAGON_SCALE,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(LootItem.lootTableItem(LRItems.DRAGON_SCALE).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))).setWeight(10))

                    )
            );

            output.accept(HEALING_TALISMAN,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(20))
                                    .add(LootItem.lootTableItem(LRItems.HEALING_TALISMAN).setWeight(10))

                    )
            );

            output.accept(VILLAGE_WEAPONSMITH_MODIFIER,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(20))
                                    .add(LootItem.lootTableItem(LRItems.SACRED_TALISMAN).setWeight(10))

                    )
            );

            output.accept(SIMPLE_DUNGEON_MODIFIER,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(10))
                                    .add(LootItem.lootTableItem(LRItems.AGGLOMERATION_MALICE).setWeight(10))

                    )
            );

            output.accept(DESERT_PYRAMID_MODIFIER,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(30))
                                    .add(LootItem.lootTableItem(LRItems.HARDENED_RING).setWeight(10))

                    )
            );

            output.accept(ANCIENT_FRAGMENT_ADD,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(90))
                                    .add(LootItem.lootTableItem(LRItems.ANCIENT_FRAGMENT).setWeight(10))
                    )
            );

            output.accept(ANCIENT_FRAGMENT_ADD_HIGH_CHANCE,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(40))
                                    .add(LootItem.lootTableItem(LRItems.ANCIENT_FRAGMENT).setWeight(10))
                    )
            );

            output.accept(DARK_STEEL_CLAW,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(30))
                                    .add(LootItem.lootTableItem(LRItems.DARK_STEEL_CLAW).setWeight(10))
                    )
            );

            output.accept(THE_ORE_COLLECTORS_RING,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(30))
                                    .add(LootItem.lootTableItem(LRItems.THE_ORE_COLLECTORS_RING).setWeight(10))
                    )
            );

            output.accept(LAVA_RING,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(10))
                                    .add(LootItem.lootTableItem(LRItems.LAVA_RING).setWeight(10))
                    )
            );

            output.accept(NETHER_RING,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(30))
                                    .add(LootItem.lootTableItem(LRItems.LAVA_RING).setWeight(10))
                    )
            );

            output.accept(NETHER_TALISMAN,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(10))
                                    .add(LootItem.lootTableItem(LRItems.NETHER_TALISMAN).setWeight(10))
                    )
            );
        }

        private static ResourceKey<LootTable> registerModifier(ResourceLocation resourceLocation) {
            return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(DataMain.modid(), "modifier/" + resourceLocation.getPath().replace(":", "_")));
        }

        private static ResourceKey<LootTable> registerModifier(String name) {
            return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(DataMain.modid(), "modifier/" + name));
        }
    }

}
