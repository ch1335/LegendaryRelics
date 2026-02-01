package com.chen1335.legendaryRelics.compat.ali;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootModifier.LootEntry;
import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.yanny.ali.api.AliEntrypoint;
import com.yanny.ali.api.IPlugin;
import com.yanny.ali.api.IServerRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AliEntrypoint
public class LegendaryRelicPlugin implements IPlugin {

    @Override
    public void registerServer(@NotNull IServerRegistry registry) {
        Map<String, List<LootEntry>> combine = new HashMap<>();
        LootModifier.LOOT_ENTRIES.values().forEach(lootEntry -> {
            for (String string : lootEntry.lootTables.value) {
                combine.computeIfAbsent(string, s -> new ArrayList<>()).add(lootEntry);
            }
        });

        combine.forEach((targetTable, lootEntries) -> {
            registry.registerLootModifiers(utils -> {
                return List.of(new LRLootModifier(utils,targetTable, lootEntries));
            });
        });

    }

    @Override
    public String getModId() {
        return LegendaryRelics.MODID;
    }
}
