package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRRecipe {
    private static final DeferredRegister<RecipeType<?>> TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, LegendaryRelics.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EquipmentWorkbenchCraft>> EQUIPMENT_CRAFT = TYPE.register("equipment_craft", () -> RecipeType.simple(LegendaryRelics.id("equipment_craft")));


    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, LegendaryRelics.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EquipmentWorkbenchCraft>> EQUIPMENT_CRAFT_SERIALIZER = RECIPE_SERIALIZER.register("equipment_craft", EquipmentWorkbenchCraft.Serializer::new);

    public static void register(IEventBus eventBus){
        TYPE.register(eventBus);
        RECIPE_SERIALIZER.register(eventBus);
    }

}
