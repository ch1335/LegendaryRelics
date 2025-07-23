package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.data.lootTables.LRGlobalLootModifierProvider;
import com.chen1335.legendaryRelics.data.lootTables.LRLootTableProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataMain {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        DatapackBuiltinEntriesProvider builtinEntriesProvider = generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(Registries.DAMAGE_TYPE, LRDamageTypes::bootstrap)
                ,
                Map.of(),
                Set.of(LegendaryRelics.MODID)
        ));
        LRBlockTagsProvider blockTagsProvider = generator.addProvider(event.includeServer(), new LRBlockTagsProvider(generator.getPackOutput(), builtinEntriesProvider.getRegistryProvider(), event.getExistingFileHelper()));


        generator.addProvider(event.includeServer(), new LRItemTagsProvider(generator.getPackOutput(), builtinEntriesProvider.getRegistryProvider(), blockTagsProvider.contentsGetter()));

        generator.addProvider(event.includeServer(), new LRItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));

        generator.addProvider(event.includeServer(), new LRGlobalLootModifierProvider(
                generator.getPackOutput(),
                builtinEntriesProvider.getRegistryProvider(),
                modid()
        ));

        generator.addProvider(event.includeServer(), new LRLootTableProvider(
                generator.getPackOutput(),
                builtinEntriesProvider.getRegistryProvider()));

        generator.addProvider(event.includeServer(), new LRRecipeProvider(
                generator.getPackOutput(),
                builtinEntriesProvider.getRegistryProvider()));

        generator.addProvider(event.includeServer(), new LRDamageTypeTagsProvider(
                generator.getPackOutput(),
                builtinEntriesProvider.getRegistryProvider(),
                event.getExistingFileHelper()));
    }

    public static String modid() {
        return LegendaryRelics.MODID;
    }
}
