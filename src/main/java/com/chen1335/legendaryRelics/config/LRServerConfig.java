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
     * Threshold factor for rare drops, relative to the average chance inside a loot pool.
     *
     * <p>An entry counts as rare when its chance is below {@code this factor / number of entries in the pool}.
     * Rare entries that do not drop get one extra independent retry, driven by the player's Magic Find attribute.
     */
    public static final ModConfigSpec.DoubleValue RARE_DROP_THRESHOLD_FACTOR;

    /**
     * Chance threshold for the probability-based loot conditions
     * ({@code random_chance}, {@code random_chance_with_enchanted_bonus}, {@code table_bonus}).
     *
     * <p>Only conditions whose base chance is below this value are boosted by the player's Magic Find attribute.
     */
    public static final ModConfigSpec.DoubleValue RARE_CONDITION_CHANCE_THRESHOLD;

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

        builder.comment("Loot settings").push("loot");

        RARE_DROP_THRESHOLD_FACTOR = builder
                .comment("Threshold factor (0~1) for rare drops, relative to the average chance inside a loot pool.",
                        "An entry counts as rare when its chance is below this factor divided by the number of entries",
                        "in the same pool. A rare entry that did not drop gets one extra independent retry, so its final",
                        "chance becomes base chance x (1 + Magic Find), capped at rare_condition_chance_threshold;",
                        "other entries are unaffected.",
                        "Default 0.5: with 25 entries in a pool the threshold is 0.5 / 25 = 2%.",
                        "Set to 0 to disable rare drop boosting entirely.")
                .defineInRange("rare_drop_threshold_factor", 0.5D, 0.0D, 1.0D);

        RARE_CONDITION_CHANCE_THRESHOLD = builder
                .comment("Chance threshold (0~1) for the probability-based loot conditions: random_chance,",
                        "random_chance_with_enchanted_bonus and table_bonus. Only conditions whose base chance is",
                        "below this value get boosted by the player's Magic Find: their final chance becomes",
                        "base chance x (1 + Magic Find), capped at this threshold. Other conditions and other loot",
                        "are unaffected.",
                        "Default 0.1.",
                        "Set to 0 to disable boosting those conditions entirely.")
                .defineInRange("rare_condition_chance_threshold", 0.1D, 0.0D, 1.0D);

        builder.pop();

        CONFIG_SPEC = builder.build();
    }
}
