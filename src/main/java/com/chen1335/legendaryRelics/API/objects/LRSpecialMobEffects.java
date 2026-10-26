package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Doom;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.EnderSuppression;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Erosion;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.InfernoScorch;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Invincibility;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Lurk;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.OrbitingFireChakramsEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.PhaseShooting;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSpecialMobEffects {
    private static final DeferredRegister<MobEffectType<?>> SPECIAL_MOB_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.SPECIAL_EFFECT_TYPE, LegendaryRelics.MODID);

    public static void register(IEventBus eventBus) {
        SPECIAL_MOB_EFFECT_TYPES.register(eventBus);
    }

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Erosion>> EROSION = SPECIAL_MOB_EFFECT_TYPES.register("erosion", MobEffectType.Builder.<Erosion>builder()
            .factory(Erosion::new)
            .renderIcon()
            .category(MobEffectCategory.HARMFUL)
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<InfernoScorch>> INFERNO_SCORCH = SPECIAL_MOB_EFFECT_TYPES.register("inferno_scorch", MobEffectType.Builder.<InfernoScorch>builder()
            .factory(InfernoScorch::new)
            .renderIcon()
            .category(MobEffectCategory.HARMFUL)
            .style(Style.EMPTY.withColor(ChatFormatting.RED))
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Doom>> DOOM = SPECIAL_MOB_EFFECT_TYPES.register("doom", MobEffectType.Builder.<Doom>builder()
            .factory(Doom::new)
            .renderIcon()
            .style(Style.EMPTY.withColor(ChatFormatting.DARK_RED))
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<PhaseShooting>> PHASE_SHOOTING = SPECIAL_MOB_EFFECT_TYPES.register("phase_shooting", MobEffectType.Builder.<PhaseShooting>builder()
            .factory(PhaseShooting::new)
            .renderIcon()
            .style(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA))
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<EnderSuppression>> ENDER_SUPPRESSION = SPECIAL_MOB_EFFECT_TYPES.register("ender_suppression", MobEffectType.Builder.<EnderSuppression>builder()
            .factory(EnderSuppression::new)
            .renderIcon()
            .category(MobEffectCategory.HARMFUL)
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Invincibility>> INVINCIBILITY = SPECIAL_MOB_EFFECT_TYPES.register("invincibility", MobEffectType.Builder.<Invincibility>builder()
            .factory(Invincibility::new)
            .renderIcon()
            .build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Slowness>> SLOWNESS = SPECIAL_MOB_EFFECT_TYPES.register("slowness", MobEffectType.Builder.<Slowness>builder()
            .factory(Slowness::new)
            .renderIcon()
            .category(MobEffectCategory.HARMFUL)
            .build());

    /** 暗影刺客套装的隐匿状态。 */
    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Lurk>> LURK = SPECIAL_MOB_EFFECT_TYPES.register("lurk", MobEffectType.Builder.<Lurk>builder()
            .factory(Lurk::new)
            .renderIcon()
            .category(MobEffectCategory.BENEFICIAL)
            .style(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA))
            .build());

    /** 烈焰飞轮的环绕调度效果：HUD 图标上显示当前飞轮数量，扇形显示剩余时间。 */
    public static final DeferredHolder<MobEffectType<?>, MobEffectType<OrbitingFireChakramsEffect>> ORBITING_FIRE_CHAKRAMS = SPECIAL_MOB_EFFECT_TYPES.register("orbiting_fire_chakrams", MobEffectType.Builder.<OrbitingFireChakramsEffect>builder()
            .factory(OrbitingFireChakramsEffect::new)
            .renderIcon()
            .build());
}
