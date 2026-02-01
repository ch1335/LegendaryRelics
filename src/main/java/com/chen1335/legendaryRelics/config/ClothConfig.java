package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.chen1335.legendaryRelics.network.LootConfigPack;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ClothConfig {
    public static void build(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            ConfigBuilder configBuilder = ConfigBuilder.create();
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
            configBuilder.setTitle(Component.translatable("legendary_relics.config"));
            configBuilder.setParentScreen(parent);
            ConfigCategory clientConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.client"));
            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.x"), com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X)
                    .setDefaultValue(0)
                    .setSaveConsumer(d -> {
                        com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X = d;
                    })
                    .build()
            );

            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.y"), Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y)
                    .setDefaultValue(0.5F)
                    .setSaveConsumer(d -> {
                        com.chen1335.legendaryRelics.config.Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y = d;
                    })
                    .build()
            );


            ConfigCategory lootTableConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.lootTable"));
            if (!canEdit(parent.getMinecraft())) {
                lootTableConfig.setDescription(new Component[]{Component.translatable("legendary_relics.config.lootTable.cant_edit")});
            }
            LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
                SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(lootEntry.getComponent());

                @NotNull StringListListEntry lootTables = entryBuilder.startStrList(Component.translatable("legendary_relics.loot_config.loot_tables"), lootEntry.lootTables.value)
                        .setDefaultValue(lootEntry.lootTables.defaultValue)
                        .setSaveConsumer(strings -> {
                            lootEntry.lootTables.value = strings;
                        })
                        .build();
                lootTables.setEditable(canEdit(parent.getMinecraft()));
                subCategoryBuilder.add(lootTables);

                @NotNull DoubleListEntry chance = entryBuilder.startDoubleField(Component.translatable("legendary_relics.loot_config.chance"), lootEntry.chance.value)
                        .setDefaultValue(lootEntry.chance.defaultValue)
                        .setSaveConsumer(d -> {
                            lootEntry.chance.value = d;
                        }).build();
                chance.setEditable(canEdit(parent.getMinecraft()));
                subCategoryBuilder.add(chance);

                @NotNull IntegerListEntry rolls = entryBuilder.startIntField(Component.translatable("legendary_relics.loot_config.rolls"), lootEntry.rolls.value)
                        .setDefaultValue(lootEntry.rolls.defaultValue)
                        .setSaveConsumer(i -> {
                            lootEntry.rolls.value = i;
                        }).build();
                rolls.setEditable(canEdit(parent.getMinecraft()));
                subCategoryBuilder.add(rolls);


                lootTableConfig.addEntry(subCategoryBuilder.build());
            });

            configBuilder.setSavingRunnable(() -> {
                Config.save();
                LocalPlayer player = parent.getMinecraft().player;
                if (player != null) {
                    if (parent.getMinecraft().getSingleplayerServer() != null) {
                        LootConfig.save();
                        if (ModList.get().isLoaded("ali")) {
                            player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.info.ali"));
                        }
                    } else if (player.getPermissionLevel() >= 2) {
                        player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.request_update"));
                        PacketDistributor.sendToServer(new LootConfigPack(LootModifier.LOOT_ENTRIES.values().stream().toList()));
                    }
                }

            });
            return configBuilder.build();
        });
    }

    private static boolean canEdit(Minecraft minecraft) {
        if (minecraft.player == null) {
            return true;
        }
        return minecraft.getSingleplayerServer() != null || minecraft.player.getPermissionLevel() >= 2;
    }
}
