package com.chen1335.legendaryRelics.data;

import com.chen1335.arcanumlib.modules.loot.lootCondition.ChanceModifier;
import com.chen1335.arcanumlib.modules.loot.lootCondition.ChanceModifierCondition;
import com.chen1335.arcanumlib.modules.loot.lootEntry.AllOfEntry;
import com.chen1335.arcanumlib.modules.loot.lootInjector.LootInjector;
import com.chen1335.arcanumlib.modules.loot.lootInjector.data.LootInjectorProvider;
import com.chen1335.arcanumlib.modules.loot.lootInjector.operator.TableOperator;
import com.chen1335.arcanumlib.modules.loot.lootInjector.selector.TableSelector;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 本模组的战利品注入器清单，由旧版战利品系统迁移而来。
 *
 * <p>一张表一个注入器，表里每个条目各自一个池。概率统一走 ChanceModifierCondition + SetChance 的替代写法
 * （基础概率直接写进条件的 base_chance 字段），带抢夺加成的再接一个 ToolEnchantmentLevel。
 */
public class LRLootInjectorProvider extends LootInjectorProvider {

    // 多张表共用的条目，写一次省得逐处重复；Drop 只是数据，被多个池复用是安全的
    // 基础 20%，开箱玩家每点幸运 +1%，先钳到 30% 再按难度放大
    private static final Drop ANCIENT_FRAGMENT_NORMAL = new Drop(0.2D, 0.0D, List.of(
            ChanceModifier.EntityAttribute.of(Attributes.LUCK, ChanceModifier.EntitySource.THIS_ENTITY,
                    ChanceModifier.Operation.ADD, 0.01D),
            ChanceModifier.Clamp.of(0.0D, 0.3D),
            ChanceModifier.DifficultyFactor.multiply(1.0D, 1.0D, 1.15D, 1.3D)),
            () -> LootItem.lootTableItem(LRItems.ANCIENT_FRAGMENT.get()));

    // 基础 30%，开箱玩家每点幸运 +1%，先钳到 40% 再按难度放大
    private static final Drop ANCIENT_FRAGMENT_HIGH = new Drop(0.3D, 0.0D, List.of(
            ChanceModifier.EntityAttribute.of(Attributes.LUCK, ChanceModifier.EntitySource.THIS_ENTITY,
                    ChanceModifier.Operation.ADD, 0.01D),
            ChanceModifier.Clamp.of(0.0D, 0.4D),
            ChanceModifier.DifficultyFactor.multiply(1.0D, 1.0D, 1.15D, 1.3D)),
            () -> LootItem.lootTableItem(LRItems.ANCIENT_FRAGMENT.get()));
    // 基础 20%，开箱玩家每点幸运 +1%，先钳到 30% 再按难度放大（暗金难度加成比其他碎片高）
    private static final Drop DARK_GOLD_FRAGMENT_NORMAL = new Drop(0.2D, 0.0D, List.of(
            ChanceModifier.EntityAttribute.of(Attributes.LUCK, ChanceModifier.EntitySource.THIS_ENTITY,
                    ChanceModifier.Operation.ADD, 0.01D),
            ChanceModifier.Clamp.of(0.0D, 0.3D),
            ChanceModifier.DifficultyFactor.multiply(1.0D, 1.0D, 1.2D, 1.4D)),
            () -> LootItem.lootTableItem(LRItems.DARK_GOLD_FRAGMENT.get()));
    // 基础 30%，开箱玩家每点幸运 +1%，先钳到 40% 再按难度放大
    private static final Drop DARK_GOLD_FRAGMENT_HIGH = new Drop(0.3D, 0.0D, List.of(
            ChanceModifier.EntityAttribute.of(Attributes.LUCK, ChanceModifier.EntitySource.THIS_ENTITY,
                    ChanceModifier.Operation.ADD, 0.01D),
            ChanceModifier.Clamp.of(0.0D, 0.4D),
            ChanceModifier.DifficultyFactor.multiply(1.0D, 1.0D, 1.2D, 1.4D)),
            () -> LootItem.lootTableItem(LRItems.DARK_GOLD_FRAGMENT.get()));


    private static final Drop NETHER_RING = drop(0.25D, LRItems.NETHER_RING);
    private static final Drop NETHER_TALISMAN = drop(0.25D, LRItems.NETHER_TALISMAN);
    private static final Drop HEALING_TALISMAN = drop(0.2D, LRItems.HEALING_TALISMAN);
    private static final Drop SKELETON_THROWING_KNIFE = drop(0.3D, LRItems.SKELETON_THROWING_KNIFE);
    private static final Drop HARDENED_RING = drop(0.25D, LRItems.HARDENED_RING);
    private static final Drop LAVA_RING = drop(0.5D, LRItems.LAVA_RING);
    private static final Drop ORE_COLLECTORS_RING = drop(0.25D, LRItems.THE_ORE_COLLECTORS_RING);
    private static final Drop DARK_STEEL_CLAW = drop(0.25D, LRItems.DARK_STEEL_CLAW);
    private static final Drop SACRED_TALISMAN = drop(0.25D, LRItems.SACRED_TALISMAN);
    private static final Drop AGGLOMERATION_MALICE = drop(0.5D, LRItems.AGGLOMERATION_MALICE);

    public LRLootInjectorProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "loot_injecter", "Loot Injectors");
    }

    /**
     * 一条掉落：基础概率 + 可选每级抢夺加成 + 额外加工器 + 【条目工厂】。
     *
     * <p>entry 必须是工厂而不是 Builder 实例：Builder 上的 when() 是往自己身上追加条件，
     * 而下面的常量会被多张表复用，共享一个实例会让条件越积越多。
     */
    private record Drop(double chance, double perLooting, List<ChanceModifier> extra,
                        Supplier<LootPoolEntryContainer.Builder<?>> entry) {
        Drop(double chance, double perLooting, Supplier<LootPoolEntryContainer.Builder<?>> entry) {
            this(chance, perLooting, List.of(), entry);
        }
    }

    @Override
    protected List<LootInjector> injectors(HolderLookup.Provider provider) {
        List<LootInjector> list = new ArrayList<>();

        list.add(merged("desert_pyramid", BuiltInLootTables.DESERT_PYRAMID,
                SKELETON_THROWING_KNIFE, HARDENED_RING, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE, HEALING_TALISMAN));
        list.add(merged("village_weaponsmith", BuiltInLootTables.VILLAGE_WEAPONSMITH,
                HEALING_TALISMAN, SACRED_TALISMAN));
        list.add(merged("village_temple", BuiltInLootTables.VILLAGE_TEMPLE, HEALING_TALISMAN));
        list.add(merged("village_armorer", BuiltInLootTables.VILLAGE_ARMORER, HEALING_TALISMAN));

        list.add(merged("bastion_treasure", BuiltInLootTables.BASTION_TREASURE,
                NETHER_TALISMAN, NETHER_RING, ANCIENT_FRAGMENT_HIGH, DARK_GOLD_FRAGMENT_HIGH));
        list.add(merged("nether_bridge", BuiltInLootTables.NETHER_BRIDGE,
                NETHER_RING, ANCIENT_FRAGMENT_NORMAL, DARK_GOLD_FRAGMENT_NORMAL));
        list.add(merged("bastion_other", BuiltInLootTables.BASTION_OTHER,
                NETHER_RING, ANCIENT_FRAGMENT_NORMAL, DARK_GOLD_FRAGMENT_NORMAL));
        list.add(merged("bastion_bridge", BuiltInLootTables.BASTION_BRIDGE,
                NETHER_RING, ANCIENT_FRAGMENT_NORMAL, DARK_GOLD_FRAGMENT_NORMAL));
        list.add(merged("bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE,
                NETHER_RING, ANCIENT_FRAGMENT_NORMAL, DARK_GOLD_FRAGMENT_NORMAL));

        list.add(merged("ruined_portal", BuiltInLootTables.RUINED_PORTAL,
                LAVA_RING, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT,
                ORE_COLLECTORS_RING, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("ancient_city", BuiltInLootTables.ANCIENT_CITY,
                DARK_STEEL_CLAW, ANCIENT_FRAGMENT_HIGH));
        list.add(merged("ancient_city_ice_box", BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
                DARK_STEEL_CLAW, ANCIENT_FRAGMENT_HIGH));
        list.add(merged("buried_treasure", BuiltInLootTables.BURIED_TREASURE, ANCIENT_FRAGMENT_HIGH));
        list.add(merged("simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON,
                AGGLOMERATION_MALICE, ANCIENT_FRAGMENT_HIGH, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("end_city_treasure", BuiltInLootTables.END_CITY_TREASURE, ANCIENT_FRAGMENT_HIGH));
        list.add(merged("stronghold_library", BuiltInLootTables.STRONGHOLD_LIBRARY, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR, ANCIENT_FRAGMENT_NORMAL));
        list.add(merged("stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING, ANCIENT_FRAGMENT_NORMAL));

        // Boss：龙鳞与凋灵之魂必掉，数量受抢夺影响
        list.add(boss("elder_guardian", EntityType.ELDER_GUARDIAN,
                new Drop(0.3D, 0.05D,
                        () -> LootItem.lootTableItem(LRItems.THUNDERBOLT_NECKLACE.get()))));
        list.add(boss("piglin_brute", EntityType.PIGLIN_BRUTE,
                new Drop(0.1D, 0.02D,
                        () -> LootItem.lootTableItem(LRItems.TYRANNICAL_ESSENCE.get()))));
        list.add(boss("ender_dragon", EntityType.ENDER_DRAGON,
                new Drop(1.0D, 0.0D, () -> LootItem.lootTableItem(LRItems.DRAGON_SCALE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                        .apply(lootingCount(provider)))));
        list.add(boss("wither", EntityType.WITHER,
                new Drop(1.0D, 0.0D, () -> LootItem.lootTableItem(LRItems.WITHER_SPIRIT.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
                        .apply(lootingCount(provider)))));

        return List.copyOf(list);
    }

    private static Drop drop(double chance, DeferredItem<? extends Item> item) {
        return new Drop(chance, 0.0D, () -> LootItem.lootTableItem(item.get()));
    }

    private static LootInjector merged(String path, ResourceKey<LootTable> table, Drop... drops) {
        return new LootInjector(LegendaryRelics.id(path),
                new TableSelector.ById(table.location()), 1000, pools(drops));
    }

    private static LootInjector boss(String path, EntityType<?> type, Drop... drops) {
        return new LootInjector(LegendaryRelics.id(path),
                new TableSelector.ById(type.getDefaultLootTable().location()), 1000, pools(drops));
    }

    /**
     * 一个池 + 一个 AllOfEntry：池掷一次必中它，它被抽中后把所有孩子都产出去。
     *
     * <p>概率条件挂在【孩子】上，所以每个孩子都是各自独立判定，而且孩子之间不抢权重。
     * 池里只有一个条目，所以权重占比恒为 1，界面里也只占一格。
     *
     * <p>不能用 minecraft:group：group 只是把孩子的权重摊平进池的候选列表，
     * LootPool.addRandomItem 随后仍然按权重抽一个，"N 个条目"会变成"每次只出 1 个"。
     */
    private static List<TableOperator> pools(Drop... drops) {
        List<LootPoolEntryContainer.Builder<?>> children = new ArrayList<>();

        for (Drop drop : drops) {
            children.add(drop.entry().get().when(chanceCondition(drop.chance(), drop.perLooting(), drop.extra())));
        }

        return List.of(new TableOperator.AddPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(AllOfEntry.allOf(children))
                .build()));
    }

    /** 概率统一由 ChanceModifierCondition 表达：基础概率走字段，链里只放抢夺加成。 */
    private static LootItemCondition.Builder chanceCondition(double chance, double perLooting,
                                                             List<ChanceModifier> extra) {
        List<ChanceModifier> modifiers = new ArrayList<>();

        if (perLooting != 0.0D) {
            modifiers.add(ChanceModifier.ToolEnchantmentLevel.of(Enchantments.LOOTING,
                    ChanceModifier.Operation.ADD, perLooting));
        }

        modifiers.addAll(extra);

        return ChanceModifierCondition.builder(chance, modifiers.toArray(ChanceModifier[]::new));
    }

    /** 抢夺让数量上限 +0~1。 */
    private static LootItemFunction.Builder lootingCount(HolderLookup.Provider provider) {
        return EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F));
    }
}
