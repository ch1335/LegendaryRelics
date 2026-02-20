package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.CooldownAbleEffectType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.BlackDragonChestPlateEffect;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.FallImmunity;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.*;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.ErosionEffect;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.Perforation;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.SoulEater;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class LREquipmentEffectTypes {
    private static final DeferredRegister<EffectType<?>> EQUIPMENT_EFFECT_TYPES = DeferredRegister.create(EERegisterTypes.EQUIPMENT_EFFECT_TYPE, LegendaryRelics.MODID);

    public static final DeferredHolder<EffectType<?>, EffectType<Redemption>> REDEMPTION = register("redemption", () -> new CooldownAbleEffectType<>(Redemption::new).cooldownIcon(LegendaryRelics.id("textures/cooldown_icons/sacred_talisman.png")));

    public static final DeferredHolder<EffectType<?>, EffectType<AttributeBoostInNether>> ATTRIBUTE_BOOST_IN_NETHER = register("attribute_boost_in_nether", () -> new EffectType<>(AttributeBoostInNether::new));

    public static final DeferredHolder<EffectType<?>, EffectType<DarkSteelClawEffect>> DARK_STEEL_CLAW_EFFECT = register("dark_steel_claw_effect", () -> new EffectType<>(DarkSteelClawEffect::new));

    public static final DeferredHolder<EffectType<?>, EffectType<InFireTargetDamageIncrease>> IN_FIRE_TARGET_DAMAGE_INCREASE = register("in_fire_target_damage_increase", () -> new EffectType<>(InFireTargetDamageIncrease::new));

    public static final DeferredHolder<EffectType<?>, EffectType<FireDamageReduce>> FIRE_DAMAGE_REDUCE = register("fire_damage_reduce", () -> new EffectType<>(FireDamageReduce::new));

    public static final DeferredHolder<EffectType<?>, EffectType<HardenedEffect>> HARDENED_EFFECT = register("hardened_effect", () -> new CooldownAbleEffectType<>(HardenedEffect::new).cooldownIcon(LegendaryRelics.id("textures/cooldown_icons/hardened_ring.png")));

    public static final DeferredHolder<EffectType<?>, EffectType<HealIncreaseEffect>> HEAL_INCREASE_EFFECT = register("heal_increase_effect", () -> new EffectType<>(HealIncreaseEffect::new, true));

    public static final DeferredHolder<EffectType<?>, EffectType<HealPerSecondEffect>> HEAL_PER_SECOND_EFFECT = register("heal_per_second_effect", () -> new EffectType<>(HealPerSecondEffect::new, true));

    public static final DeferredHolder<EffectType<?>, EffectType<PerseveranceEffect>> PERSEVERANCE_EFFECT = register("perseverance_effect", () -> new EffectType<>(PerseveranceEffect::new));

    public static final DeferredHolder<EffectType<?>, EffectType<ShieldRegeneratorEffect>> SHIELD_REGENERATOR_EFFECT = register("shield_regenerator_effect", () -> new CooldownAbleEffectType<>(ShieldRegeneratorEffect::new).cooldownIcon(LegendaryRelics.id("textures/cooldown_icons/shield_regenerator.png")));

    public static final DeferredHolder<EffectType<?>, EffectType<OreCollectorEffect>> ORE_COLLECTOR_EFFECT = register("ore_collector_effect", () -> new EffectType<>(OreCollectorEffect::new));

    public static final DeferredHolder<EffectType<?>, EffectType<AgglomerationMaliceEffect>> AGGLOMERATION_MALICE_EFFECT = register("agglomeration_malice_effect", () -> new EffectType<>(AgglomerationMaliceEffect::new));

    public static final DeferredHolder<EffectType<?>, EffectType<ErosionEffect>> EROSION_EFFECT = register("erosion_effect", () -> new EffectType<>(ErosionEffect::new));

    public static final DeferredHolder<EffectType<?>, EffectType<BlackDragonChestPlateEffect>> BLACK_DRAGON_CHESTPLATE_EFFECT = register("black_dragon_chestplate_effect", () -> new CooldownAbleEffectType<>(BlackDragonChestPlateEffect::new).cooldownIcon(LegendaryRelics.id("textures/cooldown_icons/black_dragon_chestplate.png")));

    public static final DeferredHolder<EffectType<?>, EffectType<FallImmunity>> FALL_IMMUNITY = register("fall_immunity", () -> new EffectType<>(FallImmunity::new));

    public static final DeferredHolder<EffectType<?>, EffectType<SoulEater>> SOUL_EATER = register("soul_eater", () -> new EffectType<>(SoulEater::new));

    public static final DeferredHolder<EffectType<?>, EffectType<FlameImmunity>> FLAME_IMMUNITY = register("flame_immunity", () -> new EffectType<>(FlameImmunity::new));

    public static final DeferredHolder<EffectType<?>, EffectType<GameTaskEffect>> GAME_TASK_CURIO = register("game_task", () -> new EffectType<>(GameTaskEffect::new, true));

    public static final DeferredHolder<EffectType<?>, EffectType<AllAttributeBoost>> ALL_ATTRIBUTE_BOOST = register("all_attribute_boost", () -> new EffectType<>(AllAttributeBoost::new, true));

    public static final DeferredHolder<EffectType<?>, EffectType<Perforation>> PERFORATION = register("perforation", () -> new EffectType<>(Perforation::new));

    private static <T extends BaseEffect> DeferredHolder<EffectType<?>, EffectType<T>> register(final String name, final Supplier<EffectType<T>> sup) {
        return EQUIPMENT_EFFECT_TYPES.register(name, sup);
    }

    public static void register(IEventBus modEventBus) {
        EQUIPMENT_EFFECT_TYPES.register(modEventBus);
    }
}
