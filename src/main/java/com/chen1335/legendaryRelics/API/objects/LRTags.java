package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface LRTags {
    interface Items {
        TagKey<Item> CAN_ONLY_WEAR_ONE = create("can_only_wear_one");

        TagKey<Item> SCALES_ENDER_DRAGON = create("scales/ender_dragon");

        static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, LegendaryRelics.id(name));
        }

        static TagKey<Item> create(String nameSpace,String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(nameSpace,name));
        }
    }
}
