package com.chen1335.legendaryRelics.common.lootModifier.lootInject;

import com.chen1335.legendaryRelics.common.lootModifier.lootInject.lootInjects.EnderDragonLootInjector;
import com.chen1335.legendaryRelics.common.lootModifier.lootInject.lootInjects.PiglinBruteLootInjector;
import com.chen1335.legendaryRelics.common.lootModifier.lootInject.lootInjects.WitherLootInjector;

import java.util.HashSet;
import java.util.Set;

public class LootInjectors {
    public static final Set<BaseInjector> INJECTORS = new HashSet<>();

    public static void load() {
        addInjector(new WitherLootInjector());
        addInjector(new EnderDragonLootInjector());
        addInjector(new PiglinBruteLootInjector());
    }

    public static void addInjector(BaseInjector baseInjector) {
        INJECTORS.add(baseInjector);
    }
}
