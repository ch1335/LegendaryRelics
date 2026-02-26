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

public class EnderDragonLootInjector extends BaseInjector {
    public EnderDragonLootInjector() {
        super(Set.of(EntityType.ENDER_DRAGON.getDefaultLootTable().location()));
    }

    @Override
    public void inject(HolderLookup.Provider registries, ResourceLocation name, LootTable table) {
        List<LootPool> pools = table.pools;
        LootPool.Builder builder = newPoolBuilder();
        builder.add(lootItem(LRItems.DRAGON_SCALE).apply(setCount(4)).apply(lootingMultiplier(registries, 0, 1)));
        pools.add(builder.build());
    }
}
