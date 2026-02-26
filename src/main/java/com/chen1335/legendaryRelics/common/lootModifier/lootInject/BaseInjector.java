package com.chen1335.legendaryRelics.common.lootModifier.lootInject;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public abstract class BaseInjector {
    private final Set<ResourceLocation> locations;

    public BaseInjector(Set<ResourceLocation> targets) {
        this.locations = targets;
    }

    public void inject(HolderLookup.Provider registries, ResourceLocation name, LootTable table) {

    }

    public boolean test(ResourceLocation resourceLocation) {
        return locations.contains(resourceLocation);
    }

    public LootPool.Builder newPoolBuilder() {
        return new LootPool.Builder();
    }

    public LootPool.Builder newPoolBuilder(String name) {
        return newPoolBuilder().name(name);
    }

    public LootPoolSingletonContainer.Builder<?> lootItem(ItemLike itemLike) {
        return LootItem.lootTableItem(itemLike);
    }

    public EnchantedCountIncreaseFunction.Builder lootingMultiplier(HolderLookup.Provider registries, float count) {
        return EnchantedCountIncreaseFunction.lootingMultiplier(registries, ConstantValue.exactly(count));
    }

    public EnchantedCountIncreaseFunction.Builder lootingMultiplier(HolderLookup.Provider registries, float from, float to) {
        return EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(from, to));
    }

    public LootItemCondition.Builder killedByPlayer() {
        return LootItemKilledByPlayerCondition.killedByPlayer();
    }

    public LootItemCondition.Builder randomChanceAndLootingBoost(HolderLookup.Provider registries, float base, float perLevelAfterFirst) {
        return LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, base, perLevelAfterFirst);
    }

    public static LootItemConditionalFunction.Builder<?> setCount(float count) {
        return SetItemCountFunction.setCount(ConstantValue.exactly(count));
    }

    public static LootItemConditionalFunction.Builder<?> setCount(float from, float to) {
        return SetItemCountFunction.setCount(UniformGenerator.between(from, to));
    }
}
