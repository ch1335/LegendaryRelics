package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 生物生成的数据生成：NeoForge 生物群系生成表（biome modifier）。
 *
 * <p>运行时的生成放置规则在 {@code common.LRSpawnEvents}，两者分工不同。
 */
public class LRSpawnDatagen {

    /** 由 {@code DataMain} 的 RegistrySetBuilder 调用。 */
    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        // 冰川骷髅：生成在冰刺之地。权重 20 —— 原版该群系怪物池总权重约 515，加上本项后约合 3.7%
        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LegendaryRelics.id("glacite_skeleton_ice_spikes")),
                BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                        HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(Biomes.ICE_SPIKES)),
                        new MobSpawnSettings.SpawnerData(LREntityTypes.GLACIATE_SKELETON.get(), 20, 1, 2)));
    }
}
