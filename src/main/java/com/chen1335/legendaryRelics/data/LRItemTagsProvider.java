package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.CuriosConstants;

import java.util.concurrent.CompletableFuture;

public class LRItemTagsProvider extends ItemTagsProvider {
    public LRItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(CuriosConstants.Tags.CHARM)
                .add(
                        LRItems.SACRED_TALISMAN.asItem(),
                        LRItems.AGGLOMERATION_MALICE.asItem(),
                        LRItems.SHIELD_REGENERATOR.asItem()
                );

        tag(CuriosConstants.Tags.RING)
                .add(
                        LRItems.HARDENED_RING.asItem()
                );
        tag(CuriosConstants.Tags.HANDS)
                .add(
                        LRItems.DARK_STEEL_CLAW.asItem()
                );

    }
}
