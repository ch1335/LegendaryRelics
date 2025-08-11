package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.util.List;
import java.util.stream.Stream;

public class LootConfig {
    public static void load() {
        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("legendary_relics_loot_config.toml"))) {
            config.load();
            load(config);
            config.save();
        }
    }

    public static void save() {
        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("legendary_relics_loot_config.toml"))) {
            load(config);
            config.save();
        }
    }

    public static void load(CommentedFileConfig config) {
        ConcurrentCommentedConfig server = ConfigUtils.get(config, "lootTable", config.createSubConfig(), "LootTable Config");
        LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
            ConcurrentCommentedConfig entry = ConfigUtils.get(server, id, config.createSubConfig(), "");
            lootEntry.chance.value = ConfigUtils.get(entry, id + "_chance", lootEntry.chance.value, "");
            lootEntry.lootTables.value = fromStringList(ConfigUtils.get(entry, id + "_loot_tables", fromResourceLocationList(lootEntry.lootTables.value), ""));
        });
    }

    public static List<ResourceLocation> fromStringList(List<String> strings) {
        return strings.stream().flatMap(s -> Stream.of(ResourceLocation.parse(s))).toList();
    }

    public static List<String> fromResourceLocationList(List<ResourceLocation> strings) {
        return strings.stream().flatMap(location -> Stream.of(location.toString())).toList();
    }
}
