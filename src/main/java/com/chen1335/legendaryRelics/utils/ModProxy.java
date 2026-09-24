package com.chen1335.legendaryRelics.utils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ModProxy {
    public static IEventBus MOD_EVENT_BUS;
    public static final List<ModProxy> MOD_PROXIES = new ArrayList<>();
    private final boolean isLoaded;



    protected ModProxy(String modid) {
        isLoaded = FMLLoader.getLoadingModList().getModFileById(modid) != null;
        if (isLoaded) {
            MOD_EVENT_BUS.addListener(this::registerClientExtensions);
        }
    }

    public boolean shouldLoad() {
        return isLoaded;
    }

    public static void addProxy(ModProxy proxy){
        MOD_PROXIES.add(proxy);
    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {

    }
}
