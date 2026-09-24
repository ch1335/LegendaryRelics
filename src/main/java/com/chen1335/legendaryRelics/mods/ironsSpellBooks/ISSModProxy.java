package com.chen1335.legendaryRelics.mods.ironsSpellBooks;

import com.chen1335.legendaryRelics.utils.ModProxy;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ISSModProxy extends ModProxy {
    public static final ModProxy INSTANCE = new ISSModProxy();

    protected ISSModProxy() {
        super("irons_spellbooks");
    }

    @Override
    public void registerClientExtensions(RegisterClientExtensionsEvent event) {

    }
}
