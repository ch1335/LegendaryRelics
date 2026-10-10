package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootCondition.ChanceModifier;
import com.chen1335.legendaryRelics.common.lootCondition.ChanceModifierCondition;
import com.chen1335.legendaryRelics.common.lootInjecter.LootInjector;
import com.chen1335.legendaryRelics.common.lootInjecter.LootInjectorOp;
import com.chen1335.legendaryRelics.common.lootInjecter.condition.TableCondition;
import com.chen1335.legendaryRelics.common.lootInjecter.datagen.LootInjectorProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
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

/**
 * 本模组的战利品注入器清单，由旧版战利品系统迁移而来。
 *
 * <p>一张表合成一个注入器、一个池：池掷一次必中 group，group 把子条目全部展开，
 * 每个子条目各自过自己的概率条件。这样多个"独立概率"的条目只需要一个池，
 * 而不是每个条目一个池 —— 池本身只认权重，表达不了独立概率。
 *
 * <p>概率统一走 ChanceModifierCondition + SetChance，带抢夺加成的再接一个 ToolEnchantmentLevel。
 */
public class LRLootInjectorProvider extends LootInjectorProvider {

    public LRLootInjectorProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "loot_injecter", "Loot Injectors");
    }

    /**
     * 一条掉落：概率 + 可选每级抢夺加成 + 条目本身。
     */
    private record Drop(double chance, double perLooting, LootPoolEntryContainer.Builder<?> entry) {
    }

    @Override
    protected List<LootInjector> injectors(HolderLookup.Provider provider) {
        List<LootInjector> list = new ArrayList<>();

        list.add(merged("desert_pyramid", BuiltInLootTables.DESERT_PYRAMID,
                drop(0.3D, LRItems.SKELETON_THROWING_KNIFE),
                drop(0.25D, LRItems.HARDENED_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("jungle_temple", BuiltInLootTables.JUNGLE_TEMPLE,
                drop(0.2D, LRItems.HEALING_TALISMAN)));
        list.add(merged("village_weaponsmith", BuiltInLootTables.VILLAGE_WEAPONSMITH,
                drop(0.2D, LRItems.HEALING_TALISMAN),
                drop(1.0D / 3.0D, LRItems.SACRED_TALISMAN)));
        list.add(merged("village_temple", BuiltInLootTables.VILLAGE_TEMPLE,
                drop(0.2D, LRItems.HEALING_TALISMAN)));
        list.add(merged("village_armorer", BuiltInLootTables.VILLAGE_ARMORER,
                drop(0.2D, LRItems.HEALING_TALISMAN)));

        list.add(merged("bastion_treasure", BuiltInLootTables.BASTION_TREASURE,
                drop(0.25D, LRItems.NETHER_TALISMAN),
                drop(0.25D, LRItems.NETHER_RING),
                drop(0.4D, LRItems.ANCIENT_FRAGMENT),
                drop(0.4D, LRItems.DARK_GOLD_FRAGMENT)));
        list.add(merged("nether_bridge", BuiltInLootTables.NETHER_BRIDGE,
                drop(0.25D, LRItems.NETHER_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT),
                drop(0.2D, LRItems.DARK_GOLD_FRAGMENT)));
        list.add(merged("bastion_other", BuiltInLootTables.BASTION_OTHER,
                drop(0.25D, LRItems.NETHER_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT),
                drop(0.2D, LRItems.DARK_GOLD_FRAGMENT)));
        list.add(merged("bastion_bridge", BuiltInLootTables.BASTION_BRIDGE,
                drop(0.25D, LRItems.NETHER_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT),
                drop(0.2D, LRItems.DARK_GOLD_FRAGMENT)));
        list.add(merged("bastion_hoglin_stable", BuiltInLootTables.BASTION_HOGLIN_STABLE,
                drop(0.25D, LRItems.NETHER_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT),
                drop(0.2D, LRItems.DARK_GOLD_FRAGMENT)));

        list.add(merged("ruined_portal", BuiltInLootTables.RUINED_PORTAL,
                drop(0.5D, LRItems.LAVA_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("abandoned_mineshaft", BuiltInLootTables.ABANDONED_MINESHAFT,
                drop(0.25D, LRItems.THE_ORE_COLLECTORS_RING),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("ancient_city", BuiltInLootTables.ANCIENT_CITY,
                drop(0.25D, LRItems.DARK_STEEL_CLAW),
                drop(0.4D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("ancient_city_ice_box", BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
                drop(0.25D, LRItems.DARK_STEEL_CLAW),
                drop(0.4D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("buried_treasure", BuiltInLootTables.BURIED_TREASURE,
                drop(0.4D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("simple_dungeon", BuiltInLootTables.SIMPLE_DUNGEON,
                drop(0.5D, LRItems.AGGLOMERATION_MALICE),
                drop(0.4D, LRItems.ANCIENT_FRAGMENT),
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("end_city_treasure", BuiltInLootTables.END_CITY_TREASURE,
                drop(0.4D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("stronghold_library", BuiltInLootTables.STRONGHOLD_LIBRARY,
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("stronghold_corridor", BuiltInLootTables.STRONGHOLD_CORRIDOR,
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));
        list.add(merged("stronghold_crossing", BuiltInLootTables.STRONGHOLD_CROSSING,
                drop(0.2D, LRItems.ANCIENT_FRAGMENT)));

        // Boss：龙鳞与凋灵之魂必掉，数量受抢夺影响
        list.add(boss("elder_guardian", EntityType.ELDER_GUARDIAN,
                new Drop(0.3D, 0.05D, LootItem.lootTableItem(LRItems.THUNDERBOLT_NECKLACE.get()))));
        list.add(boss("piglin_brute", EntityType.PIGLIN_BRUTE,
                new Drop(0.1D, 0.02D, LootItem.lootTableItem(LRItems.TYRANNICAL_ESSENCE.get()))));
        list.add(boss("ender_dragon", EntityType.ENDER_DRAGON,
                new Drop(1.0D, 0.0D, LootItem.lootTableItem(LRItems.DRAGON_SCALE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                        .apply(lootingCount(provider)))));
        list.add(boss("wither", EntityType.WITHER,
                new Drop(1.0D, 0.0D, LootItem.lootTableItem(LRItems.WITHER_SPIRIT.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
                        .apply(lootingCount(provider)))));

        return List.copyOf(list);
    }

    private static Drop drop(double chance, DeferredItem<? extends Item> item) {
        return new Drop(chance, 0.0D, LootItem.lootTableItem(item.get()));
    }

    private static LootInjector merged(String path, net.minecraft.resources.ResourceKey<LootTable> table, Drop... drops) {
        return new LootInjector(LegendaryRelics.id(path),
                new TableCondition.ById(table.location()), 1000,
                pools(drops));
    }

    private static LootInjector boss(String path, EntityType<?> type, Drop... drops) {
        return new LootInjector(LegendaryRelics.id(path),
                new TableCondition.ById(type.getDefaultLootTable().location()), 1000,
                pools(drops));
    }

    /**
     * 一个条目一个池：池掷一次、池里唯一条目，权重占比恒为 1，概率条件直接决定它是否产出。
     *
     * <p>不能用 minecraft:group 合并：group 只是把孩子的权重摊平进池的候选列表，
     * LootPool.addRandomItem 随后仍然按权重抽一个，所以合并会让"N 个条目"变成"每次只出 1 个"。
     */
    private static List<LootInjectorOp> pools(Drop... drops) {
        List<LootInjectorOp> ops = new ArrayList<>();

        for (Drop drop : drops) {
            ops.add(new LootInjectorOp.AddPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(drop.entry())
                    .when(chanceCondition(drop.chance(), drop.perLooting()))
                    .build()));
        }

        return ops;
    }

    /**
     * 概率统一由 ChanceModifierCondition 表达：先 SetChance 定值，再按需叠抢夺加成。
     */
    private static LootItemCondition.Builder chanceCondition(double chance, double perLooting) {
        List<ChanceModifier> modifiers = new ArrayList<>();

        if (perLooting != 0.0D) {
            modifiers.add(new ChanceModifier.ToolEnchantmentLevel(Enchantments.LOOTING,
                    ChanceModifier.Operation.ADD, perLooting));
        }

        // 基础概率写在条件字段上，链里只放加工器
        return ChanceModifierCondition.builder(chance, modifiers.toArray(ChanceModifier[]::new));
    }

    /**
     * 抢夺让数量上限 +0~1。
     */
    private static LootItemFunction.Builder lootingCount(HolderLookup.Provider provider) {
        return EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F));
    }
}