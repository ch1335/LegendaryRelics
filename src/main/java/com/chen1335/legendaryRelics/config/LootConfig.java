package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class LootConfig {

    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("legendary_relics");
        path.toFile().mkdirs();
        try (CommentedFileConfig config = CommentedFileConfig.of(path.resolve("legendary_relics_loot_config.toml"))) {
            config.load();
            load(config);
            config.save();
        }
    }

    public static void save() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("legendary_relics");
        path.toFile().mkdirs();
        try (CommentedFileConfig config = CommentedFileConfig.of(path.resolve("legendary_relics_loot_config.toml"))) {
            load(config);
            config.save();
        }
    }

    public static void load(CommentedFileConfig config) {
        ConcurrentCommentedConfig server = ConfigUtils.get(config, "lootTable", config.createSubConfig(), "LootTable Config");
        LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
            if (lootEntry.isCreatedByRemote) {
                return;
            }
            ConcurrentCommentedConfig entry = ConfigUtils.get(server, id, config.createSubConfig(), "");
            lootEntry.chance.value = ConfigUtils.get(entry, "chance", lootEntry.chance.value, "");
            lootEntry.rolls.value = ConfigUtils.get(entry, "rolls", lootEntry.rolls.value, "");
            lootEntry.lootTables.value = ConfigUtils.get(entry, "loot_tables", lootEntry.lootTables.value, "");
        });
    }

    public static List<ResourceLocation> fromStringList(List<String> strings) {
        return strings.stream().flatMap(s -> Stream.of(ResourceLocation.parse(s))).toList();
    }

    public static List<String> fromResourceLocationList(List<ResourceLocation> strings) {
        return strings.stream().flatMap(location -> Stream.of(location.toString())).toList();
    }
}
