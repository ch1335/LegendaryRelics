package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.clientExtensions.BlackDragonArmorExtension;
import com.chen1335.legendaryRelics.client.clientExtensions.InfernoArmorExtension;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ClientExtensionsRegister {
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new BlackDragonArmorExtension(),
                LRItems.BLACK_DRAGON_HELMET,
                LRItems.BLACK_DRAGON_CHEST_PLATE,
                LRItems.BLACK_DRAGON_LEGGINGS,
                LRItems.BLACK_DRAGON_BOOTS
        );

        event.registerItem(new InfernoArmorExtension(),
                LRItems.INFERNO_HELMET,
                LRItems.INFERNO_CHEST_PLATE,
                LRItems.INFERNO_LEGGINGS,
                LRItems.INFERNO_BOOTS
        );
    }

}
