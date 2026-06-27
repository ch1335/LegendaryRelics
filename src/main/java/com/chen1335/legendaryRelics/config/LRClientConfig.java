package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LRClientConfig {
    public static final ModConfigSpec CONFIG_SPEC;
    public static final HUD HUD;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        HUD = new HUD(builder);
        CONFIG_SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading ev) {

    }

    public static class HUD {

        public ModConfigSpec.DoubleValue equipment_effect_cooldown_x;

        public ModConfigSpec.DoubleValue equipment_effect_cooldown_y;

        public HUD(ModConfigSpec.Builder builder) {
            equipment_effect_cooldown_x = builder
                    .comment("The x-axis of the percentage of the rendering cooling position to the total screen size")
                    .defineInRange("equipment_effect_cooldown_x_percentage", 0D, 0D, 1D);

            equipment_effect_cooldown_y = builder
                    .comment("The x-axis of the percentage of the rendering cooling position to the total screen size")
                    .defineInRange("equipment_effect_cooldown_y_percentage", 0.5D, 0D, 1D);
        }
    }
}
