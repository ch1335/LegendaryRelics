package com.chen1335.legendaryRelics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server-side config.
 *
 * <p>Originally an empty stub (never registered into {@code modContainer}, with no entries at all).
 * Server-side values related to gameplay and world generation live here.
 */
public class LRServerConfig {
    public static final ModConfigSpec CONFIG_SPEC;

    /**
     * Base chance for an ice spike to contain an ice encasement holding Zenith Ice.
     *
     * <p>Actual chance = this value + ice spike maximum radius x {@link #ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS}.
     */
    public static final ModConfigSpec.DoubleValue ZENITH_ICE_SPIKE_CHANCE;

    /**
     * Extra chance added for each point of the ice spike's maximum radius.
     *
     * <p>The maximum radius is 1~3, corresponding to a horizontal span of 3~7 blocks,
     * so thicker spikes are more likely to contain Zenith Ice.
     */
    public static final ModConfigSpec.DoubleValue ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS;

    /**
     * Weight-share threshold for rare mobs.
     *
     * <p>Only mobs whose base spawn weight is below this share of the whole spawn list get their weight
     * boosted by the players' Seeking attribute; the final weight is base weight x (1 + Seeking), uncapped.
     */
    public static final ModConfigSpec.DoubleValue RARE_SPAWN_WEIGHT_THRESHOLD;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("World generation settings").push("world_gen");

        ZENITH_ICE_SPIKE_CHANCE = builder
                .comment("Base chance (0~1) for an ice spike to contain an ice encasement holding Zenith Ice.",
                        "Actual chance = this value + ice spike maximum radius x the per-radius increase below,",
                        "so thicker ice spikes are more likely to contain Zenith Ice.",
                        "Default 0.025.")
                .defineInRange("zenith_ice_spike_chance", 0.025D, 0.0D, 1.0D);

        ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS = builder
                .comment("Extra chance (0~1) added for each point of the ice spike's maximum radius.",
                        "The maximum radius is 1~3, corresponding to a horizontal span of 3~7 blocks.",
                        "Default 0.005: with the default base chance 0.025 that means 0.030 for radius 1,",
                        "0.035 for radius 2 and 0.040 for radius 3.",
                        "Set to 0 to make every ice spike equally likely.")
                .defineInRange("zenith_ice_spike_chance_per_radius", 0.005D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("Mob spawn settings").push("spawn");

        RARE_SPAWN_WEIGHT_THRESHOLD = builder
                .comment("Weight-share threshold (0~1) for rare mobs. A mob counts as rare when its base spawn",
                        "weight is below this share of the whole spawn list. Rare mobs get their weight boosted",
                        "by the highest Seeking attribute among players within 128 blocks: final weight becomes",
                        "base weight x (1 + Seeking), uncapped. Other mobs are unaffected.",
                        "Default 0.02 (2%).",
                        "Set to 0 to disable the Seeking attribute entirely.")
                .defineInRange("rare_spawn_weight_threshold", 0.02D, 0.0D, 1.0D);

        builder.pop();

        CONFIG_SPEC = builder.build();
    }
}
