package com.chen1335.legendaryRelics;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.legendaryRelics.API.objects.*;
import com.chen1335.legendaryRelics.client.ClientExtensionsRegister;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.LegendaryTooltipsHandler;
import com.chen1335.legendaryRelics.client.particlePlayer.ParticlePlayersHolder;
import com.chen1335.legendaryRelics.common.calculator.AutoRegister;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.lootModifier.LootEntries;
import com.chen1335.legendaryRelics.config.ClothConfig;
import com.chen1335.legendaryRelics.config.Config;
import com.chen1335.legendaryRelics.config.LootConfig;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.GameTaskEffect;
import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.Environment;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(LegendaryRelics.MODID)
public class LegendaryRelics {
    public static final String MODID = "legendary_relics";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LEGENDARY_RELICS = CREATIVE_MODE_TABS.register("legendary_relics_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.legendary_relics"))
            .icon(() -> LRItems.SACRED_TALISMAN.value().getDefaultInstance())
            .displayItems((parameters, output) -> {
                LRItems.ITEM_DEFERRED_REGISTER.getEntries().forEach(itemDeferredHolder -> {
                    output.accept(itemDeferredHolder.value().getDefaultInstance());
                });
            }).build());

    public static boolean APOTHIC_ATTRIBUTES_EXTENSION_LOADED = false;

    public static final Path CONFIGS_PATH = FMLPaths.CONFIGDIR.get().resolve("legendary_relics");

    public LegendaryRelics(IEventBus modEventBus, ModContainer modContainer) {
        CONFIGS_PATH.toFile().mkdirs();

        EquipmentEffectLib.init(modEventBus, modContainer);
        SpecialEffectLib.init(modEventBus, modContainer);

        CREATIVE_MODE_TABS.register(modEventBus);
        LRItems.ITEM_DEFERRED_REGISTER.register(modEventBus);
        LRShieldType.SHIELD_TYPE_DEFERRED_REGISTER.register(modEventBus);
        LRAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        LRDataComponentTypes.DATA_COMPONENTS.register(modEventBus);
        LRArmorMaterials.ARMOR_MATERIAL_DEFERRED_REGISTER.register(modEventBus);
        LREquipmentEffectTypes.EQUIPMENT_EFFECT_TYPES.register(modEventBus);
        LRSpecialMobEffect.SPECIAL_MOB_EFFECT_TYPES.register(modEventBus);
        LREntityTypes.ENTITY_TYPES.register(modEventBus);
        LRSetsEffects.SETS_EFFECTS.register(modEventBus);
        modEventBus.addListener(ClientExtensionsRegister::register);
        modEventBus.addListener(EntityRendererRegister::registerLayerDefinitions);
        modEventBus.addListener(EntityRendererRegister::addLayers);
        modEventBus.addListener(this::clientInit);
        modEventBus.addListener(this::setup);
        NeoForge.EVENT_BUS.addListener(this::serverAboutToStartEvent);
        if (ModList.get().isLoaded("cloth_config")) {
            if (Environment.get().getDist().isClient()) {
                ClothConfig.build(modContainer);
            }
        }

        if (ModList.get().isLoaded("apothic_attributes_extension")) {
            APOTHIC_ATTRIBUTES_EXTENSION_LOADED = true;
        }

        CalculatorRegister.init();
        AutoRegister.init();
    }

    public static boolean isApothicAttributesExtensionLoaded() {
        return APOTHIC_ATTRIBUTES_EXTENSION_LOADED;
    }

    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }

    public void clientInit(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("legendarytooltips")) {
            LegendaryTooltipsHandler.init();
        }
        ParticlePlayersHolder.init();

        ItemProperties.register(LRItems.CHARM_OF_FRESH_START.asItem(),
                id("all_task_finished"),
                (stack, level, entity, seed) -> {
                    GameTaskEffect effect = EquipmentEffectAPI.getEffect(stack, LREquipmentEffectTypes.GAME_TASK_CURIO.get());
                    if (effect == null) {
                        return 0;
                    }
                    return effect.getRawEffectLevel() >= GameTaskEffect.TASKS.size() ? 1 : 0;
                });

        ItemProperties.register(
                LRItems.LAST_WHISPER.asItem(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, living, i) -> {
                    if (living == null) {
                        return 0.0F;
                    } else {
                        return living.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F;
                    }
                });
        ItemProperties.register(
                LRItems.LAST_WHISPER.asItem(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, living, i) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F
        );
    }


    public void setup(FMLCommonSetupEvent event) {
        GameTaskEffect.initTasks();
        Config.load();
        event.enqueueWork(LootEntries::init);
        event.enqueueWork(LootConfig::load);
    }

    public void serverAboutToStartEvent(ServerAboutToStartEvent event) {

    }
}
