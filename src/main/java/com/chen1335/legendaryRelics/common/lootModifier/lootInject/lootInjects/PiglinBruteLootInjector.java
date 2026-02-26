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

public class PiglinBruteLootInjector extends BaseInjector {
    public PiglinBruteLootInjector() {
        super(Set.of(EntityType.PIGLIN_BRUTE.getDefaultLootTable().location()));
    }

    @Override
    public void inject(HolderLookup.Provider registries, ResourceLocation name, LootTable table) {
        List<LootPool> pools = table.pools;
        LootPool.Builder builder = newPoolBuilder();
        builder.add(
                lootItem(LRItems.TYRANNICAL_ESSENCE)
                        .when(killedByPlayer())
                        .when(randomChanceAndLootingBoost(registries, 0.1F, 0.02F))
        );
        pools.add(builder.build());
    }
}
