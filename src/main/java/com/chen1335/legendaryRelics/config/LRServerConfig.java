package com.chen1335.legendaryRelics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class LRServerConfig {
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        CONFIG_SPEC = builder.build();
    }
}
