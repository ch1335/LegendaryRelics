package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.armorSetEffect.SoulArmorSetEffect;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.entities.projectiles.SoulMissile;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.equipmentEffects.MagicSuppressionEffect;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulBoots;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulChestPlate;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulHelmet;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet.SoulLeggings;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.weapons.MagicBreaker;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects.MagicSuppression;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects.SoulGather;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class ISSRegisters {

    public static class Item {
        private static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            ITEM_DEFERRED_REGISTER.register(modEventBus);
        }

        public static Collection<DeferredHolder<net.minecraft.world.item.Item, ? extends net.minecraft.world.item.Item>> getEntries() {
            return ITEM_DEFERRED_REGISTER.getEntries();
        }

        public static final DeferredItem<SoulHelmet> SOUL_HELMET = ITEM_DEFERRED_REGISTER.register("soul_helmet", SoulHelmet::new);

        public static final DeferredItem<SoulChestPlate> SOUL_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("soul_chest_plate", SoulChestPlate::new);

        public static final DeferredItem<SoulLeggings> SOUL_LEGGINGS = ITEM_DEFERRED_REGISTER.register("soul_leggings", SoulLeggings::new);

        public static final DeferredItem<SoulBoots> SOUL_BOOTS = ITEM_DEFERRED_REGISTER.register("soul_boots", SoulBoots::new);

        public static final DeferredItem<MagicBreaker> MAGIC_BREAKER = ITEM_DEFERRED_REGISTER.register("magic_breaker", MagicBreaker::new);
    }

    public static class Entity {
        private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            ENTITY_TYPES.register(modEventBus);
        }

        public static final DeferredHolder<EntityType<?>, EntityType<SoulMissile>> SOUL_MISSILE = ENTITY_TYPES.register("soul_missile",
                () -> EntityType.Builder.<SoulMissile>of(SoulMissile::new, MobCategory.MISC).sized(0.25F, 0.25F).updateInterval(1).build("soul_missile"));
    }

    public static class ArmorSetEffect {
        private static final DeferredRegister<SetEffect> SETS_EFFECTS = DeferredRegister.create(EERegisterTypes.SETS_EFFECT_TYPE_KEY, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            SETS_EFFECTS.register(modEventBus);
        }

        public static final DeferredHolder<SetEffect, SoulArmorSetEffect> SOUL_ARMOR = SETS_EFFECTS.register("soul_armor", SoulArmorSetEffect::new);
    }

    public static class EquipmentEffect {
        private static final DeferredRegister<EffectType<?>> EQUIPMENT_EFFECT_TYPES = DeferredRegister.create(EERegisterTypes.EQUIPMENT_EFFECT_TYPE, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            EQUIPMENT_EFFECT_TYPES.register(modEventBus);
        }

        public static final DeferredHolder<EffectType<?>, EffectType<MagicSuppressionEffect>> MAGIC_SUPPRESSION = EQUIPMENT_EFFECT_TYPES.register("magic_suppression", () -> new EffectType<>(MagicSuppressionEffect::new));
    }

    public static class SpecialMobEffect {
        private static final DeferredRegister<MobEffectType<?>> SPECIAL_MOB_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.SPECIAL_EFFECT_TYPE, LegendaryRelics.MODID);

        public static void register(IEventBus modEventBus) {
            SPECIAL_MOB_EFFECT_TYPES.register(modEventBus);
        }

        public static final DeferredHolder<MobEffectType<?>, MobEffectType<SoulGather>> SOUL_GATHER = SPECIAL_MOB_EFFECT_TYPES.register("soul_gather", MobEffectType.Builder.<SoulGather>builder()
                .factory(SoulGather::new)
                .renderIcon()
                .build());

        public static final DeferredHolder<MobEffectType<?>, MobEffectType<MagicSuppression>> MAGIC_SUPPRESSION = SPECIAL_MOB_EFFECT_TYPES.register("magic_suppression", MobEffectType.Builder.<MagicSuppression>builder()
                .factory(MagicSuppression::new)
                .renderIcon()
                .category(MobEffectCategory.HARMFUL)
                .build());
    }
}
