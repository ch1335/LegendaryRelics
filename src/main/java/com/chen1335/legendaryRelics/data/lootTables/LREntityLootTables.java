package com.chen1335.legendaryRelics.data.lootTables;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * 实体战利品表。
 *
 * <p>冰川骷髅单独掉一把霜锥：基础 5%，每级抢夺 +1%。手持那把永不掉落（见 {@code GlaciteSkeleton#finalizeSpawn}），
 * 所以玩家只能从这里拿到，且是全新的。
 */
public class LREntityLootTables extends EntityLootSubProvider {

    protected LREntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(LREntityTypes.GLACITE_SKELETON.get(), LootTable.lootTable().withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(LRItems.FROST_SHARD.get())
                                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05F, 0.01F)))));
    }

    /** 只登记本模组的实体，否则会因原版实体缺表而报错。 */
    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(LREntityTypes.GLACITE_SKELETON.get());
    }
}
