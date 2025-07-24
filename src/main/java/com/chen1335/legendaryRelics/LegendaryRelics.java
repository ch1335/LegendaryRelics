package com.chen1335.legendaryRelics;

import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.legendaryRelics.API.objects.*;
import com.chen1335.legendaryRelics.client.ClientExtensionsRegister;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.LegendaryTooltipsHandler;
import com.chen1335.legendaryRelics.config.Config;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.mojang.logging.LogUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(LegendaryRelics.MODID)
public class LegendaryRelics {
    public static final String MODID = "legendary_relics";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LEGENDARY_RELICS = CREATIVE_MODE_TABS.register("legendary_relics", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.legendary_relics"))
            .icon(() -> LRItems.SACRED_TALISMAN.value().getDefaultInstance())
            .displayItems((parameters, output) -> {
                LRItems.ITEM_DEFERRED_REGISTER.getEntries().forEach(itemDeferredHolder -> {
                    output.accept(itemDeferredHolder.value().getDefaultInstance());
                });
            }).build());

    public LegendaryRelics(IEventBus modEventBus, ModContainer modContainer) {
        Config.load();

        new EquipmentEffectLib(modEventBus, modContainer);
        new SpecialEffectLib(modEventBus, modContainer);

        CREATIVE_MODE_TABS.register(modEventBus);
        LRItems.ITEM_DEFERRED_REGISTER.register(modEventBus);
        LRShieldType.SHIELD_TYPE_DEFERRED_REGISTER.register(modEventBus);
        LRAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        LRDataComponentTypes.DATA_COMPONENTS.register(modEventBus);
        LRArmorMaterials.ARMOR_MATERIAL_DEFERRED_REGISTER.register(modEventBus);
        LREquipmentEffectTypes.EQUIPMENT_EFFECT_TYPES.register(modEventBus);
        LRSpecialMobEffect.SPECIAL_MOB_EFFECT_TYPES.register(modEventBus);
        LREntityTypes.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ClientExtensionsRegister::register);
        modEventBus.addListener(EntityRendererRegister::registerLayerDefinitions);
        modEventBus.addListener(EntityRendererRegister::addLayers);
        modEventBus.addListener(this::clientInit);


        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            ConfigBuilder configBuilder = ConfigBuilder.create();
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
            configBuilder.setTitle(Component.translatable("legendary_relics.config"));
            configBuilder.setParentScreen(parent);
            ConfigCategory clientConfig = configBuilder.getOrCreateCategory(Component.translatable("legendary_relics.config.client"));
             clientConfig.addEntry(entryBuilder.startDoubleField(  Component.translatable("legendary_relics.cooldown.x"), Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X)
                    .setDefaultValue(0)
                    .setSaveConsumer(d -> {
                        Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_X = d;
                    })
                    .build()
            );

            clientConfig.addEntry(entryBuilder.startDoubleField(Component.translatable("legendary_relics.cooldown.y"), Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y)
                    .setDefaultValue(0.5F)
                    .setSaveConsumer(d -> {
                        Config.ClientConfig.EQUIPMENT_EFFECT_COOLDOWN_Y = d;
                    })
                    .build()
            );
            configBuilder.setSavingRunnable(Config::save);
            return configBuilder.build();
        });
    }


    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }

    public void clientInit(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("legendarytooltips")) {
            LegendaryTooltipsHandler.init();
        }
    }
}
