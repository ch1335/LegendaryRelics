package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.menus.EquipmentWorkbenchCraftMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRMenus {
    private static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(Registries.MENU, LegendaryRelics.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<EquipmentWorkbenchCraftMenu>> EQUIPMENT_WORKBENCH_CRAFT = MENU.register("equipment_workbench_craft", () -> new MenuType<>(EquipmentWorkbenchCraftMenu::new, FeatureFlagSet.of()));

    public static void register(IEventBus modEventBus) {
        MENU.register(modEventBus);
    }
}
