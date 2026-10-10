package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootCondition.lootEntry.AllOfEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 原版的 LootPoolEntries.register 是私有的，所以自己注册。 */
public final class LRLootPoolEntries {
    public static final DeferredRegister<LootPoolEntryType> ENTRY_TYPES =
            DeferredRegister.create(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, LegendaryRelics.MODID);

    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> ALL_OF =
            ENTRY_TYPES.register("all_of", () -> new LootPoolEntryType(AllOfEntry.CODEC));

    private LRLootPoolEntries() {
    }
}