package com.chen1335.legendaryRelics.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClothConfig {
    public static void build(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            ConfigBuilder configBuilder = ConfigBuilder.create();
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
            configBuilder.setTitle(Component.translatable("legendary_relics.config"));
            configBuilder.setParentScreen(parent);
            ConfigCategory clientConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.client"));
            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.x"), LRClientConfig.HUD.equipment_effect_cooldown_x.getAsDouble())
                    .setDefaultValue(0)
                    .setSaveConsumer(d -> {
                        LRClientConfig.HUD.equipment_effect_cooldown_x.set(d);
                    })
                    .build()
            );
            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.y"), LRClientConfig.HUD.equipment_effect_cooldown_y.getAsDouble())
                    .setDefaultValue(0.5F)
                    .setSaveConsumer(d -> {
                        LRClientConfig.HUD.equipment_effect_cooldown_y.set(d);
                    })
                    .build()
            );

            configBuilder.setSavingRunnable(() -> {
                LRClientConfig.CONFIG_SPEC.save();
                LocalPlayer player = parent.getMinecraft().player;
                if (player != null && parent.getMinecraft().getSingleplayerServer() != null && ModList.get().isLoaded("ali")) {
                    player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.info.ali"));
                }
            });
            return configBuilder.build();
        });
    }
}
