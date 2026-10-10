package com.chen1335.legendaryRelics.config;

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
            ConfigCategory serverConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.server"));
            boolean serverConfigsLoaded = LRServerConfig.CONFIG_SPEC.isLoaded();
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

            DoubleListEntry rareSpawnWeightThreshold = entryBuilder.startDoubleField(
                            Component.translatable("legendary_relics.config.rare_spawn_weight_threshold"),
                            serverConfigsLoaded ? LRServerConfig.RARE_SPAWN_WEIGHT_THRESHOLD.get()
                                    : LRServerConfig.RARE_SPAWN_WEIGHT_THRESHOLD.getDefault())
                    .setDefaultValue(0.02D)
                    .setMin(0.0D)
                    .setMax(1.0D)
                    .setTooltip(Component.translatable("legendary_relics.config.rare_spawn_weight_threshold.tooltip"))
                    .build();
            rareSpawnWeightThreshold.setEditable(editable);
            serverConfig.addEntry(rareSpawnWeightThreshold);


            configBuilder.setSavingRunnable(() -> {
                LRClientConfig.CONFIG_SPEC.save();
                LocalPlayer player = parent.getMinecraft().player;
                if (player != null) {
                    if (parent.getMinecraft().getSingleplayerServer() != null) {
                        // 单人游戏里本地就是服务端，直接写入并落盘
                        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.set(zenithIceSpikeChance.getValue());
                        LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.set(zenithIceChancePerRadius.getValue());
                        LRServerConfig.RARE_SPAWN_WEIGHT_THRESHOLD.set(rareSpawnWeightThreshold.getValue());
                        LRServerConfig.CONFIG_SPEC.save();
                        if (ModList.get().isLoaded("ali")) {
                            player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.info.ali"));
                        }
                    } else if (player.getPermissionLevel() >= 2) {
                        player.sendSystemMessage(Component.translatable("legendary_relics.loot_config.request_update"));
                        // 多人游戏：服务端配置由服务端写入，这里只把值发过去
                        player.sendSystemMessage(Component.translatable("legendary_relics.config.request_update"));
                        PacketDistributor.sendToServer(new ServerConfigPack(zenithIceSpikeChance.getValue(), zenithIceChancePerRadius.getValue(), rareSpawnWeightThreshold.getValue()));
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
