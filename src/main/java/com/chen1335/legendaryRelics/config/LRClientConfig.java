package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
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
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {

        }
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

    public static double EQUIPMENT_EFFECT_COOLDOWN_X = 0F;

    public static double EQUIPMENT_EFFECT_COOLDOWN_Y = 0.5F;

    public static void load(CommentedFileConfig config) {
        ConcurrentCommentedConfig client = ConfigUtils.get(config, "client", config.createSubConfig(), "Client Config");
        EQUIPMENT_EFFECT_COOLDOWN_X = ConfigUtils.get(client, "equipment_effect_cooldown_x_percentage", EQUIPMENT_EFFECT_COOLDOWN_X, "The x-axis of the percentage of the rendering cooling position to the total screen size");
        EQUIPMENT_EFFECT_COOLDOWN_Y = ConfigUtils.get(client, "equipment_effect_cooldown_y_percentage", EQUIPMENT_EFFECT_COOLDOWN_Y, "The y-axis of the percentage of the rendering cooling position to the total screen size");
    }
}
