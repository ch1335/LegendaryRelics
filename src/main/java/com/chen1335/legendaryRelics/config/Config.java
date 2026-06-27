package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public class Config {

    public static void load() {
        try (CommentedFileConfig config = CommentedFileConfig.of(LegendaryRelics.CONFIGS_PATH.resolve("legendary_relics.toml"))) {
            config.load();
            CommonConfig.load(config);
            LRClientConfig.load(config);
            config.save();
        }
    }

    public static void save() {
        try (CommentedFileConfig config = CommentedFileConfig.of(LegendaryRelics.CONFIGS_PATH.resolve("legendary_relics.toml"))) {
            CommonConfig.load(config);
            LRClientConfig.load(config);
            config.save();
        }
    }

    public static class CommonConfig {
        public static void load(CommentedFileConfig config) {

        }
    }
}
