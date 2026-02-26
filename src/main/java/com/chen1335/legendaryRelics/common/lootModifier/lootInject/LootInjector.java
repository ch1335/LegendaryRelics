package com.chen1335.legendaryRelics.common.lootModifier.lootInject;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LootInjector {
    @SubscribeEvent
    public static void LootTableLoadEvent(LootTableLoadEvent event) {
        HolderLookup.Provider registries = event.getRegistries();
        ResourceLocation name = event.getName();
        LootTable table = event.getTable();
        for (BaseInjector injector : LootInjectors.INJECTORS) {
            if (injector.test(name)) {
                injector.inject(registries, name, table);
            }
        }
    }
}
