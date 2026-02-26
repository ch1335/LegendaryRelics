package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosTags;

import java.util.concurrent.CompletableFuture;

public class LRItemTagsProvider extends ItemTagsProvider {
    public LRItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(LRTags.Items.CAN_ONLY_WEAR_ONE).add(
                LRItems.CHARM_OF_FRESH_START.asItem()
        );

        tag(CuriosTags.CHARM)
                .add(
                        LRItems.AGGLOMERATION_MALICE.asItem(),
                        LRItems.NETHER_TALISMAN.asItem(),
                        LRItems.PURGATORY_TALISMAN.asItem(),
                        LRItems.CHARM_OF_FRESH_START.asItem()
                );

        tag(CuriosTags.RING)
                .add(
                        LRItems.HARDENED_RING.asItem(),
                        LRItems.THE_ORE_COLLECTORS_RING.asItem(),
                        LRItems.LAVA_RING.asItem(),
                        LRItems.NETHER_RING.asItem()
                );

        tag(CuriosTags.HANDS)
                .add(
                        LRItems.DARK_STEEL_CLAW.asItem()
                );

        tag(CuriosTags.BELT)
                .add(
                        LRItems.SHIELD_REGENERATOR.asItem()
                );

        tag(CuriosTags.NECKLACE).add(
                LRItems.SACRED_TALISMAN.asItem(),
                LRItems.PERSEVERANCE_NECKLACE.asItem(),
                LRItems.HEALING_TALISMAN.asItem()
        );

        tag(ItemTags.HEAD_ARMOR).add(
                LRItems.BLACK_DRAGON_HELMET.value(),
                LRItems.INFERNO_HELMET.value(),
                LRItems.TWISTED_HELMET.value()
        );

        tag(ItemTags.CHEST_ARMOR).add(
                LRItems.BLACK_DRAGON_CHEST_PLATE.value(),
                LRItems.INFERNO_CHEST_PLATE.value(),
                LRItems.TWISTED_CHEST_PLATE.value()

        );

        tag(ItemTags.LEG_ARMOR).add(
                LRItems.BLACK_DRAGON_LEGGINGS.value(),
                LRItems.INFERNO_LEGGINGS.value(),
                LRItems.TWISTED_LEGGINGS.value()
        );

        tag(ItemTags.FOOT_ARMOR).add(
                LRItems.BLACK_DRAGON_BOOTS.value(),
                LRItems.INFERNO_BOOTS.value(),
                LRItems.TWISTED_BOOTS.value()
        );

        tag(ItemTags.SWORDS).add(
                LRItems.WITHERING_BLADE.value(),
                LRItems.REAPER.value(),
                LRItems.SKELETON_THROWING_KNIFE.asItem()
        );

        tag(Tags.Items.TOOLS_BOW).add(
                LRItems.LAST_WHISPER.asItem()
        );

        tag(ItemTags.BOW_ENCHANTABLE).add(
                LRItems.LAST_WHISPER.asItem()
        );
    }
}
