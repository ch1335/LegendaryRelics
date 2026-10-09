package com.chen1335.legendaryRelics.common.lootInjecter;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * 战利品注入器的接线：注册读取监听器，并在数据全部加载完之后统一应用。
 *
 * <p>不用 LootTableLoadEvent：那个是在注册表反序列化过程中逐表触发的，那时我们的定义还不一定读完了。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LootInjectorEvents {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new LootInjectorReloadListener());
    }

    /**
     * 必须用 ServerStartingEvent + HIGHEST：ALI 在 ServerStartingEvent（默认优先级）里读表并建好界面数据，
     * 比 ServerStartedEvent 早一个事件，晚一步改就永远显示不到。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStarting(ServerStartingEvent event) {
        LootInjectorApplier.applyAll(event.getServer());
    }
}
