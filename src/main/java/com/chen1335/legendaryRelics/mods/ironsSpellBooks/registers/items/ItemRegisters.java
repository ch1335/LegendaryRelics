package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulBoots;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulChestPlate;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulHelmet;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulLeggings;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class ItemRegisters {
    private static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(LegendaryRelics.MODID);

    public static void register(IEventBus modEventBus) {
        ITEM_DEFERRED_REGISTER.register(modEventBus);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEM_DEFERRED_REGISTER.getEntries();
    }

    public static final DeferredItem<SoulHelmet> SOUL_HELMET = ITEM_DEFERRED_REGISTER.register("soul_helmet", SoulHelmet::new);

    public static final DeferredItem<SoulChestPlate> SOUL_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("soul_chest_plate", SoulChestPlate::new);

    public static final DeferredItem<SoulLeggings> SOUL_LEGGINGS = ITEM_DEFERRED_REGISTER.register("soul_leggings", SoulLeggings::new);

    public static final DeferredItem<SoulBoots> SOUL_BOOTS = ITEM_DEFERRED_REGISTER.register("soul_boots", SoulBoots::new);
}
