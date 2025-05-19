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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class LRArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL_DEFERRED_REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, LegendaryRelics.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BLACK_DRAGON = register("black_dragon", Util.make(new EnumMap<>(ArmorItem.Type.class), p_323380_ -> {
                p_323380_.put(ArmorItem.Type.BOOTS, 5);
                p_323380_.put(ArmorItem.Type.LEGGINGS, 8);
                p_323380_.put(ArmorItem.Type.CHESTPLATE, 10);
                p_323380_.put(ArmorItem.Type.HELMET, 5);
                p_323380_.put(ArmorItem.Type.BODY, 12);
            }), 20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            2.0F,
            0.0F,
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
