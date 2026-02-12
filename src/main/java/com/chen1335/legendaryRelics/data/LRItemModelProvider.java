package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class LRItemModelProvider extends ItemModelProvider {
    public LRItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LegendaryRelics.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(LRItems.SACRED_TALISMAN.asItem());
        basicItem(LRItems.AGGLOMERATION_MALICE.asItem());
        basicItem(LRItems.SHIELD_REGENERATOR.asItem());
        basicItem(LRItems.HARDENED_RING.asItem());
        basicItem(LRItems.ANCIENT_FRAGMENT.asItem());
        basicItem(LRItems.DARK_STEEL_CLAW.asItem());
        basicItem(LRItems.THE_ORE_COLLECTORS_RING.asItem());
        basicItem(LRItems.BLACK_DRAGON_HELMET.asItem());
        basicItem(LRItems.BLACK_DRAGON_CHEST_PLATE.asItem());
        basicItem(LRItems.BLACK_DRAGON_LEGGINGS.asItem());
        basicItem(LRItems.BLACK_DRAGON_BOOTS.asItem());
        basicItem(LRItems.LAVA_RING.asItem());
        basicItem(LRItems.NETHER_RING.asItem());
        basicItem(LRItems.NETHER_TALISMAN.asItem());
        basicItem(LRItems.PURGATORY_TALISMAN.asItem());
        basicItem(LRItems.HEALING_TALISMAN.asItem());
        basicItem(LRItems.PERSEVERANCE_NECKLACE.asItem());
        basicItem(LRItems.DRAGON_SCALE.asItem());
        basicItem(LRItems.DARK_GOLD_FRAGMENT.asItem());
        basicItem(LRItems.DARK_GOLD.asItem());
        basicItem(LRItems.DARK_GOLD_FORGING_TOOL.asItem());
        basicItem(LRItems.WITHER_SPIRIT.asItem());

        basicItem(LRItems.INFERNO_HELMET.asItem());
        basicItem(LRItems.INFERNO_CHEST_PLATE.asItem());
        basicItem(LRItems.INFERNO_LEGGINGS.asItem());
        basicItem(LRItems.INFERNO_BOOTS.asItem());

        basicItem(LRItems.TWISTED_HELMET.asItem());
        basicItem(LRItems.TWISTED_CHEST_PLATE.asItem());
        basicItem(LRItems.TWISTED_LEGGINGS.asItem());
        basicItem(LRItems.TWISTED_BOOTS.asItem());

        handheldItem(LRItems.WITHERING_BLADE.asItem());
        basicItem(LegendaryRelics.id("book"));



    }
}
