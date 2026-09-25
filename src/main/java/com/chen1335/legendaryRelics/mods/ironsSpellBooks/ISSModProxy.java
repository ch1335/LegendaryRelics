package com.chen1335.legendaryRelics.mods.ironsSpellBooks;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.clientExtensions.SoulArmorExtension;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.ItemRegisters;
import com.chen1335.legendaryRelics.utils.ModProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ISSModProxy extends ModProxy {
    public static final ModProxy INSTANCE = new ISSModProxy();

    protected ISSModProxy() {
        super("irons_spellbooks");
    }

    @Override
    public void init(IEventBus modEventBus) {
        ItemRegisters.register(modEventBus);
        modEventBus.addListener(this::buildCreativeTabContents);
    }

    @Override
    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new SoulArmorExtension(),
                ItemRegisters.SOUL_HELMET,
                ItemRegisters.SOUL_CHEST_PLATE,
                ItemRegisters.SOUL_LEGGINGS,
                ItemRegisters.SOUL_BOOTS
        );
    }

    private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(LegendaryRelics.LEGENDARY_RELICS.getKey())) {
            return;
        }
        ItemRegisters.getEntries().forEach(itemDeferredHolder -> {
            event.accept(itemDeferredHolder.value().getDefaultInstance());
        });
    }
}
