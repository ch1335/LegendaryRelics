package com.chen1335.legendaryRelics.config;

import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class Config {
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("legendary_relics");
        path.toFile().mkdirs();
        try (CommentedFileConfig config = CommentedFileConfig.of(path.resolve("legendary_relics.toml"))) {
            config.load();
            ClientConfig.load(config);
            config.save();
        }
    }

    public static void save() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("legendary_relics");
        path.toFile().mkdirs();
        try (CommentedFileConfig config = CommentedFileConfig.of(path.resolve("legendary_relics.toml"))) {
            ClientConfig.load(config);
            config.save();
        }
    }

    public static class ClientConfig {
        public static double EQUIPMENT_EFFECT_COOLDOWN_X = 0F;

        public static double EQUIPMENT_EFFECT_COOLDOWN_Y = 0.5F;

        public static void load(CommentedFileConfig config) {
            ConcurrentCommentedConfig client = ConfigUtils.get(config, "client", config.createSubConfig(), "Client Config");
            EQUIPMENT_EFFECT_COOLDOWN_X = ConfigUtils.get(client, "equipment_effect_cooldown_x_percentage", EQUIPMENT_EFFECT_COOLDOWN_X, "The x-axis of the percentage of the rendering cooling position to the total screen size");
            EQUIPMENT_EFFECT_COOLDOWN_Y = ConfigUtils.get(client, "equipment_effect_cooldown_y_percentage", EQUIPMENT_EFFECT_COOLDOWN_Y, "The y-axis of the percentage of the rendering cooling position to the total screen size");
        }
    }
}
