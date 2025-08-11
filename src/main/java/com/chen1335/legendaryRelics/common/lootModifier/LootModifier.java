package com.chen1335.legendaryRelics.common.lootModifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.HashMap;
import java.util.Map;

public class LootModifier {
    public static final Map<String, LootEntry> LOOT_ENTRIES = new HashMap<>();

    public static void modify(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (lootTableId == null) {
            return;
        }
        for (LootEntry lootEntry : LOOT_ENTRIES.values()) {
            lootEntry.run(lootTableId, generatedLoot);
        }
    }

    public static CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        LOOT_ENTRIES.forEach((id, lootEntry) -> {
            compoundTag.put(id, lootEntry.save());
        });
        return compoundTag;
    }
}
