package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class LRArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL_DEFERRED_REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, LegendaryRelics.MODID);

    public static void register(IEventBus modEventBus) {
        ARMOR_MATERIAL_DEFERRED_REGISTER.register(modEventBus);
    }

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLACK_DRAGON = register("black_dragon", Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                enumMap.put(ArmorItem.Type.BOOTS, 6);
                enumMap.put(ArmorItem.Type.LEGGINGS, 8);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 10);
                enumMap.put(ArmorItem.Type.HELMET, 6);
                enumMap.put(ArmorItem.Type.BODY, 12);
            }), 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5.0F,
            0.1F,
            () -> Ingredient.EMPTY
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> INFERNO = register("inferno", Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                enumMap.put(ArmorItem.Type.BOOTS, 3);
                enumMap.put(ArmorItem.Type.LEGGINGS, 6);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 8);
                enumMap.put(ArmorItem.Type.HELMET, 3);
                enumMap.put(ArmorItem.Type.BODY, 11);
            }), 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            2.5F,
            0.05F,
            () -> Ingredient.EMPTY
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TWISTED = register("twisted", Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                enumMap.put(ArmorItem.Type.BOOTS, 3);
                enumMap.put(ArmorItem.Type.LEGGINGS, 5);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 7);
                enumMap.put(ArmorItem.Type.HELMET, 3);
                enumMap.put(ArmorItem.Type.BODY, 11);
            }), 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            2.5F,
            0.05F,
            () -> Ingredient.EMPTY
    );

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngridient
    ) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, defense.get(armoritem$type));
        }
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace(name)));
        return ARMOR_MATERIAL_DEFERRED_REGISTER.register(name, () -> new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance));
    }
}
