package com.chen1335.legendaryRelics.config;

import com.chen1335.equipmentEffectLib.utils.Cast;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootModifier.LootEntries;
import com.chen1335.legendaryRelics.common.lootModifier.LootEntry;
import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.chen1335.legendaryRelics.utils.valueHolder.ConfigValueHolder;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.stream.Stream;
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LootConfig {

    public static final ModConfigSpec CONFIG_SPEC;

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {

    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        LootEntries.init();
        LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
            if (lootEntry.isCreatedByRemote) {
                return;
            }
            new LootConfigEntry(builder, id, lootEntry);
        });
        CONFIG_SPEC = builder.build();
    }

    public static List<String> fromResourceLocationList(List<ResourceLocation> strings) {
        return strings.stream().flatMap(location -> Stream.of(location.toString())).toList();
    }

    public static class LootConfigEntry {
        public final ModConfigSpec.DoubleValue chance;
        public final ModConfigSpec.IntValue rolls;
        public final ModConfigSpec.ConfigValue<List<String>> lootTables;

        public LootConfigEntry(ModConfigSpec.Builder builder, String name, LootEntry lootEntry) {
            builder.push(name);
            chance = builder.defineInRange("chance", lootEntry.chance.geDefault(), 0, Double.MAX_VALUE);
            rolls = builder.defineInRange("rolls", lootEntry.rolls.geDefault(), 0, Integer.MAX_VALUE);
            lootTables = Cast.cast(builder.defineList("loot_tables", lootEntry.lootTables.geDefault(), () -> "", s -> s instanceof String));
            builder.pop();
            lootEntry.chance.setInnerHolder(ConfigValueHolder.of(chance));
            lootEntry.rolls.setInnerHolder(ConfigValueHolder.of(rolls));
            lootEntry.lootTables.setInnerHolder(ConfigValueHolder.of(lootTables));
        }
    }
}
