package com.chen1335.legendaryRelics.common.lootModifier.lootInject.lootInjects;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.lootModifier.lootInject.BaseInjector;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Set;

public class WitherLootInjector extends BaseInjector {
    public WitherLootInjector() {
        super(Set.of(EntityType.WITHER.getDefaultLootTable().location()));
    }

    @Override
    public void inject(HolderLookup.Provider registries, ResourceLocation name, LootTable table) {
        List<LootPool> pools = table.pools;
        LootPool.Builder builder = newPoolBuilder();
        builder.add(lootItem(LRItems.WITHER_SPIRIT).apply(setCount(3)).apply(lootingMultiplier(registries, 0, 1)));
        pools.add(builder.build());
    }
}
