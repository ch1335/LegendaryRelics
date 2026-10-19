package com.chen1335.legendaryRelics.mods.legendaryRelics.registers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.LRSimpleTier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class Tier {

    public static class Weapon {

        /**
         * 骸骨飞刀的武器 tier。
         */
        public static final LRSimpleTier SKELETON_THROWING_KNIFE = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_IRON_TOOL,
                512,
                6.0F,
                5.0F,
                -2F,
                14,
                () -> Ingredient.of(Tags.Items.BONES)
        );

        /**
         * 影匕的武器 tier。
         */
        public static final LRSimpleTier SHADOW_DAGGER = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_IRON_TOOL,
                512 + 256,
                6.0F,
                6,
                -2,
                14,
                () -> Ingredient.of(Tags.Items.OBSIDIANS)
        );

        /**
         * 枯萎的武器 tier，沿用下界合金的耐久/挖掘等级/附魔性，攻击伤害加成并入 tier。
         */
        public static final LRSimpleTier WITHERING_BLADE = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                2031,
                9.0F,
                10.0F,
                -2.4F,
                15,
                () -> Ingredient.of(Items.NETHERITE_INGOT)
        );

        /**
         * 收割者的武器 tier，同枯萎。
         */
        public static final LRSimpleTier REAPER = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                2031,
                9.0F,
                12.0F,
                -2.4F,
                15,
                () -> Ingredient.of(Items.NETHERITE_INGOT)
        );

        /**
         * 霜锥的武器 tier，攻击伤害与攻速对齐原版三叉戟。
         */
        public static final LRSimpleTier FROST_SHARD = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                2031,
                7.0F,
                8.0F,
                -2F,
                15,
                () -> Ingredient.of(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE)
        );

        /**
         * 烈焰飞轮的武器 tier：轮刃手感，攻速比剑快、单次伤害略低。
         */
        public static final LRSimpleTier BLAZING_CHAKRAM = new LRSimpleTier(
                BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                2031,
                9.0F,
                7.0F,
                -1.8F,
                15,
                () -> Ingredient.of(Items.BLAZE_ROD)
        );
    }

    public static class Armor {
        private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            ARMOR_MATERIALS.register(modEventBus);
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
                }), 15,
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
                }), 15,
                SoundEvents.ARMOR_EQUIP_LEATHER,
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
            return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance));
        }
    }
}
