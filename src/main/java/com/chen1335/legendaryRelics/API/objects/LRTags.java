package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface LRTags {
    interface Items {
        TagKey<Item> CAN_ONLY_WEAR_ONE = create("can_only_wear_one");

        static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, LegendaryRelics.id(name));
        }
    }
}
