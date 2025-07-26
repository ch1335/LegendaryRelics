package com.chen1335.legendaryRelics.client.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClothConfig {
    public static void build(ModContainer modContainer){
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            ConfigBuilder configBuilder = ConfigBuilder.create();
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
            configBuilder.setTitle(Component.translatable("legendary_relics.config"));
            configBuilder.setParentScreen(parent);
            ConfigCategory clientConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.client"));
            clientConfig.addEntry(entryBuilder.startDoubleField(  Component.translatable("legendary_relics.cooldown.x"), com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X)
                    .setDefaultValue(0)
                    .setSaveConsumer(d -> {
                        com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X = d;
                    })
                    .build()
            );

            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.y"), com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y)
                    .setDefaultValue(0.5F)
                    .setSaveConsumer(d -> {
                        com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y = d;
                    })
                    .build()
            );
            configBuilder.setSavingRunnable(com.chen1335.legendaryRelics.config.Config::save);
            return configBuilder.build();
        });
    }
}
