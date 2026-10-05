package com.chen1335.legendaryRelics.data.lootTables;

import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LRLootTableProvider extends LootTableProvider {
    public LRLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(LREntityLootTables::new, LootContextParamSets.ENTITY)
        ), registries);
    }

    public static class BlockLoot extends BlockLootSubProvider {

        protected BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            dropSelf(LRBlocks.EQUIPMENT_WORKBENCH.value());

            // 冰封方块：方块自身不掉落，内容物由方块自己弹出（见 IceEncasement#playerWillDestroy）。
            // 这里挂一张空战利品表 —— 什么都不掉，但必须存在，否则数据生成会因缺少战利品表而失败。
            add(LRBlocks.ICE_ENCASEMENT.value(), LootTable.lootTable());
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return LRBlocks.holders().stream().map(DeferredHolder::value).collect(Collectors.toSet());
        }
    }

}
