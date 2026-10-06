package com.chen1335.legendaryRelics.config;

import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import com.chen1335.legendaryRelics.network.LootConfigPack;
import com.chen1335.legendaryRelics.network.ServerConfigPack;
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

            // ---- 服务端配置 ----
            // SERVER 配置在进入世界之前不会被加载，此时 ConfigValue#get() 会抛异常，
            // 所以主菜单里只显示默认值、且不可编辑。
            // 多人游戏里这份本地副本由 ServerConfigPack 在进服时同步过来。
            boolean serverConfigsLoaded = LRServerConfig.CONFIG_SPEC.isLoaded() && LootConfig.CONFIG_SPEC.isLoaded();
            ConfigCategory serverConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.server"));
            boolean editable = canEdit(parent.getMinecraft()) && serverConfigsLoaded;
            if (!editable) {
                serverConfig.setDescription(new Component[]{Component.translatable(
                        serverConfigsLoaded ? "legendary_relics.config.server.cant_edit" : "legendary_relics.config.not_in_world")});
            }

            DoubleListEntry zenithIceSpikeChance = entryBuilder.startDoubleField(
                            Component.translatable("legendary_relics.config.zenith_ice_spike_chance"),
                            serverConfigsLoaded ? LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.get()
                                    : LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.getDefault())
                    .setDefaultValue(0.025D)
                    .setMin(0.0D)
                    .setMax(1.0D)
                    .setTooltip(Component.translatable("legendary_relics.config.zenith_ice_spike_chance.tooltip"))
                    .build();
            zenithIceSpikeChance.setEditable(editable);
            serverConfig.addEntry(zenithIceSpikeChance);

            DoubleListEntry zenithIceChancePerRadius = entryBuilder.startDoubleField(
                            Component.translatable("legendary_relics.config.zenith_ice_spike_chance_per_radius"),
                            serverConfigsLoaded ? LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.get()
                                    : LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.getDefault())
                    .setDefaultValue(0.005D)
                    .setMin(0.0D)
                    .setMax(1.0D)
                    .setTooltip(Component.translatable("legendary_relics.config.zenith_ice_spike_chance_per_radius.tooltip"))
                    .build();
            zenithIceChancePerRadius.setEditable(editable);
            serverConfig.addEntry(zenithIceChancePerRadius);

            DoubleListEntry rareDropThresholdFactor = entryBuilder.startDoubleField(
                            Component.translatable("legendary_relics.config.rare_drop_threshold_factor"),
                            serverConfigsLoaded ? LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.get()
                                    : LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.getDefault())
                    .setDefaultValue(0.5D)
                    .setMin(0.0D)
                    .setMax(1.0D)
                    .setTooltip(Component.translatable("legendary_relics.config.rare_drop_threshold_factor.tooltip"))
                    .build();
            rareDropThresholdFactor.setEditable(editable);
            serverConfig.addEntry(rareDropThresholdFactor);

            DoubleListEntry rareConditionChanceThreshold = entryBuilder.startDoubleField(
                            Component.translatable("legendary_relics.config.rare_condition_chance_threshold"),
                            serverConfigsLoaded ? LRServerConfig.RARE_CONDITION_CHANCE_THRESHOLD.get()
                                    : LRServerConfig.RARE_CONDITION_CHANCE_THRESHOLD.getDefault())
                    .setDefaultValue(0.1D)
                    .setMin(0.0D)
                    .setMax(1.0D)
                    .setTooltip(Component.translatable("legendary_relics.config.rare_condition_chance_threshold.tooltip"))
                    .build();
            rareConditionChanceThreshold.setEditable(editable);
            serverConfig.addEntry(rareConditionChanceThreshold);

            ConfigCategory lootTableConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.lootTable"));
            if (!editable) {
                lootTableConfig.setDescription(new Component[]{Component.translatable(
                        serverConfigsLoaded ? "legendary_relics.config.lootTable.cant_edit" : "legendary_relics.config.not_in_world")});
            }
            LootModifier.LOOT_ENTRIES.forEach((id, lootEntry) -> {
                SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(lootEntry.getComponent());

                @NotNull StringListListEntry lootTables = entryBuilder.startStrList(Component.translatable("legendary_relics.loot_config.loot_tables"),
                                serverConfigsLoaded ? lootEntry.lootTables.get() : lootEntry.lootTables.geDefault())
                        .setDefaultValue(lootEntry.lootTables.geDefault())
                        .setSaveConsumer(lootEntry.lootTables::set)
                        .build();
                lootTables.setEditable(editable);
                subCategoryBuilder.add(lootTables);

                @NotNull DoubleListEntry chance = entryBuilder.startDoubleField(Component.translatable("legendary_relics.loot_config.chance"),
                                serverConfigsLoaded ? lootEntry.chance.get() : lootEntry.chance.geDefault())
                        .setDefaultValue(lootEntry.chance.geDefault())
                        .setSaveConsumer(lootEntry.chance::set).build();
                chance.setEditable(editable);
                subCategoryBuilder.add(chance);

                @NotNull IntegerListEntry rolls = entryBuilder.startIntField(Component.translatable("legendary_relics.loot_config.rolls"),
                                serverConfigsLoaded ? lootEntry.rolls.get() : lootEntry.rolls.geDefault())
                        .setDefaultValue(lootEntry.rolls.geDefault())
                        .setSaveConsumer(lootEntry.rolls::set).build();
                rolls.setEditable(editable);
                subCategoryBuilder.add(rolls);


                lootTableConfig.addEntry(subCategoryBuilder.build());
            });

            configBuilder.setSavingRunnable(() -> {
                LRClientConfig.CONFIG_SPEC.save();
                LocalPlayer player = parent.getMinecraft().player;
                if (player != null) {
                    if (parent.getMinecraft().getSingleplayerServer() != null) {
                        LootConfig.CONFIG_SPEC.save();
                        // 单人游戏里本地就是服务端，直接写入并落盘
                        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.set(zenithIceSpikeChance.getValue());
                        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.set(zenithIceChancePerRadius.getValue());
                        LRServerConfig.RARE_DROP_THRESHOLD_FACTOR.set(rareDropThresholdFactor.getValue());
                        LRServerConfig.RARE_CONDITION_CHANCE_THRESHOLD.set(rareConditionChanceThreshold.getValue());
                        LRServerConfig.CONFIG_SPEC.save();
                        if (ModList.get().isLoaded("ali")) {
                            player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.info.ali"));
                        }
                    } else if (player.getPermissionLevel() >= 2) {
                        player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.request_update"));
                        PacketDistributor.sendToServer(new LootConfigPack(LootModifier.LOOT_ENTRIES.values().stream().toList()));
                        // 多人游戏：服务端配置由服务端写入，这里只把值发过去
                        player.sendSystemMessage(Component.translatable("legendary_relics.config.request_update"));
                        PacketDistributor.sendToServer(new ServerConfigPack(zenithIceSpikeChance.getValue(), zenithIceChancePerRadius.getValue(), rareDropThresholdFactor.getValue(), rareConditionChanceThreshold.getValue()));
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
