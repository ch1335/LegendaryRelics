package com.chen1335.legendaryRelics.API;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ILootTableTags {
    TagKey<LootTable> TEST = TagKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "attribute_sort"));

}
