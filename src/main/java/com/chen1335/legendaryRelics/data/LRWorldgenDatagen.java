package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRFeatures;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

/**
 * 大冰刺的世界生成数据：配置地物、放置地物，以及把它加进冰刺之地的 biome modifier。
 *
 * <p>放置参数照抄原版 {@code minecraft:ice_spike}：count 3 + in_square + MOTION_BLOCKING 高度图 + biome 过滤。
 */
public class LRWorldgenDatagen {

    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_ICE_SPIKE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, LegendaryRelics.id("large_ice_spike"));

    public static final ResourceKey<PlacedFeature> LARGE_ICE_SPIKE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, LegendaryRelics.id("large_ice_spike"));

    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(LARGE_ICE_SPIKE_CONFIGURED, new ConfiguredFeature<>(
                LRFeatures.LARGE_ICE_SPIKE.get(), NoneFeatureConfiguration.INSTANCE));
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        context.register(LARGE_ICE_SPIKE_PLACED, new PlacedFeature(
                context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(LARGE_ICE_SPIKE_CONFIGURED),
                List.of(
                        // 原版 ice_spike 是每区块 3 个；这里 1/7 区块出 1 个，约合原版的 1/21
                        RarityFilter.onAverageOnceEvery(7),
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
                        BiomeFilter.biome())));
    }

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LegendaryRelics.id("large_ice_spike_ice_spikes")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(Biomes.ICE_SPIKES)),
                        HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(LARGE_ICE_SPIKE_PLACED)),
                        GenerationStep.Decoration.SURFACE_STRUCTURES));
    }
}
