package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.clientExtensions.BlackDragonArmorExtension;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ClientExtensionsRegister {
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new BlackDragonArmorExtension(),
                LRItems.BLACK_DRAGON_HELMET,
                LRItems.BLACK_DRAGON_CHEST_PLATE
        );
    }

}
