package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.ExtendedWeaponTier;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ISSTiers {
    public static class Weapon {

        /**
         * 破魔的武器 tier：耐久、攻击伤害加成、攻击速度、附魔性，以及附加的法术属性。
         */
        public static final ExtendedWeaponTier MAGIC_BREAKER = new ExtendedWeaponTier(
                2031,
                11.0F,
                -2.5F,
                12,
                BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
                () -> Ingredient.of(Items.DIAMOND),
                new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        );
    }

    public static class Armor {
        private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            ARMOR_MATERIALS.register(modEventBus);
        }

        /**
         * 灵魂盔甲的护甲材质。
         */
        public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SOUL = ARMOR_MATERIALS.register("soul", () -> new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                    enumMap.put(ArmorItem.Type.BOOTS, 3);
                    enumMap.put(ArmorItem.Type.LEGGINGS, 5);
                    enumMap.put(ArmorItem.Type.CHESTPLATE, 7);
                    enumMap.put(ArmorItem.Type.HELMET, 3);
                    enumMap.put(ArmorItem.Type.BODY, 11);
                }),
                20,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("soul"))),
                2.5F,
                0.05F
        ));
    }
}
