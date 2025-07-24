package com.chen1335.legendaryRelics.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.loading.FMLPaths;

public class Config {
    public static void load() {
        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("legendary_relics.toml"))) {
            config.load();
            ClientConfig.load(config);
            config.save();
        }
    }

    public static void save() {
        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("legendary_relics.toml"))) {
            ClientConfig.load(config);
            config.save();
        }
    }

    public static class ClientConfig {
        public static double EQUIPMENT_EFFECT_COOLDOWN_X = 0F;
        public static double EQUIPMENT_EFFECT_COOLDOWN_Y = 0.5F;

        public static void load(CommentedFileConfig config) {
            ConcurrentCommentedConfig client = get(config, "client", config.createSubConfig(), "Client Config");
            EQUIPMENT_EFFECT_COOLDOWN_X = get(client, "equipment_effect_cooldown_x_percentage", EQUIPMENT_EFFECT_COOLDOWN_X, "The x-axis of the percentage of the rendering cooling position to the total screen size");
            EQUIPMENT_EFFECT_COOLDOWN_Y = get(client, "equipment_effect_cooldown_y_percentage", EQUIPMENT_EFFECT_COOLDOWN_Y, "The y-axis of the percentage of the rendering cooling position to the total screen size");
        }
    }

    private static <T> T get(CommentedConfig config, String string, T defaultValue) {
        T value = config.get(string);
        if (value == null) {
            config.set(string, defaultValue);
            return defaultValue;
        }
        return value;
    }

    private static <T> T get(CommentedConfig config, String name, T defaultValue, String comment) {
        T value = config.get(name);
        if (value == null) {
            config.set(name, defaultValue);
            config.setComment(name, comment);
            return defaultValue;
        }
        return value;
    }
}
