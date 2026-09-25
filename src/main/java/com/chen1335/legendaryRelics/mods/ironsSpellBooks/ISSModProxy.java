package com.chen1335.legendaryRelics.mods.ironsSpellBooks;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.clientExtensions.SoulArmorExtension;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
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
        ISSRegisters.Item.register(modEventBus);
        modEventBus.addListener(this::buildCreativeTabContents);
    }

    @Override
    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new SoulArmorExtension(),
                ISSRegisters.Item.SOUL_HELMET,
                ISSRegisters.Item.SOUL_CHEST_PLATE,
                ISSRegisters.Item.SOUL_LEGGINGS,
                ISSRegisters.Item.SOUL_BOOTS
        );
    }

    private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(LegendaryRelics.LEGENDARY_RELICS.getKey())) {
            return;
        }
        ISSRegisters.Item.getEntries().forEach(itemDeferredHolder -> {
            event.accept(itemDeferredHolder.value().getDefaultInstance());
        });
    }
}
