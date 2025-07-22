package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.CooldownAbleEffectType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.*;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.ErosionEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LREquipmentEffectTypes {
    public static DeferredRegister<EffectType<?>> EQUIPMENT_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.EQUIPMENT_EFFECT_TYPE, LegendaryRelics.MODID);

    public static DeferredHolder<EffectType<?>, EffectType<Redemption>> REDEMPTION = EQUIPMENT_EFFECT_TYPES.register("redemption", () -> new CooldownAbleEffectType<Redemption>(Redemption::new, EntityEquipmentEffectData.EquipmentType.CURIO).cooldownIcon(LegendaryRelics.id("textures/item/sacred_talisman.png")));

    public static DeferredHolder<EffectType<?>, EffectType<AttributeBoostInNether>> ATTRIBUTE_BOOST_IN_NETHER = EQUIPMENT_EFFECT_TYPES.register("attribute_boost_in_nether", () -> new EffectType<>(AttributeBoostInNether::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<DarkSteelClawEffect>> DARK_STEEL_CLAW_EFFECT = EQUIPMENT_EFFECT_TYPES.register("dark_steel_claw_effect", () -> new EffectType<>(DarkSteelClawEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<InFireTargetDamageIncrease>> IN_FIRE_TARGET_DAMAGE_INCREASE = EQUIPMENT_EFFECT_TYPES.register("in_fire_target_damage_increase", () -> new EffectType<>(InFireTargetDamageIncrease::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<FireDamageReduce>> FIRE_DAMAGE_REDUCE = EQUIPMENT_EFFECT_TYPES.register("fire_damage_reduce", () -> new EffectType<>(FireDamageReduce::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<HardenedEffect>> HARDENED_EFFECT = EQUIPMENT_EFFECT_TYPES.register("hardened_effect", () -> new CooldownAbleEffectType<HardenedEffect>(HardenedEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO).cooldownIcon(LegendaryRelics.id("textures/item/hardened_ring.png")));

    public static DeferredHolder<EffectType<?>, EffectType<HealIncreaseEffect>> HEAL_INCREASE_EFFECT = EQUIPMENT_EFFECT_TYPES.register("heal_increase_effect", () -> new EffectType<>(HealIncreaseEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO, true));

    public static DeferredHolder<EffectType<?>, EffectType<HealPerSecondEffect>> HEAL_PER_SECOND_EFFECT = EQUIPMENT_EFFECT_TYPES.register("heal_per_second_effect", () -> new EffectType<>(HealPerSecondEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO, true));

    public static DeferredHolder<EffectType<?>, EffectType<PerseveranceEffect>> PERSEVERANCE_EFFECT = EQUIPMENT_EFFECT_TYPES.register("perseverance_effect", () -> new EffectType<>(PerseveranceEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<ShieldRegeneratorEffect>> SHIELD_REGENERATOR_EFFECT = EQUIPMENT_EFFECT_TYPES.register("shield_regenerator_effect", () -> new EffectType<>(ShieldRegeneratorEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<OreCollectorEffect>> ORE_COLLECTOR_EFFECT = EQUIPMENT_EFFECT_TYPES.register("ore_collector_effect", () -> new EffectType<>(OreCollectorEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<AgglomerationMaliceEffect>> AGGLOMERATION_MALICE_EFFECT = EQUIPMENT_EFFECT_TYPES.register("agglomeration_malice_effect", () -> new EffectType<>(AgglomerationMaliceEffect::new, EntityEquipmentEffectData.EquipmentType.CURIO));

    public static DeferredHolder<EffectType<?>, EffectType<ErosionEffect>> EROSION_EFFECT = EQUIPMENT_EFFECT_TYPES.register("erosion_effect", () -> new EffectType<>(ErosionEffect::new, EntityEquipmentEffectData.EquipmentType.WEAPON));

}
