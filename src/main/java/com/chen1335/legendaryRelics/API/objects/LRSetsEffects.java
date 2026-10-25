package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.ShadowAssassinArmorSetEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.TwistedArmorSetEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSetsEffects {
    private static final DeferredRegister<SetEffect> SETS_EFFECTS = DeferredRegister.create(EERegisterTypes.SETS_EFFECT_TYPE_KEY, LegendaryRelics.MODID);

    public static void register(IEventBus eventBus){
        SETS_EFFECTS.register(eventBus);
    }

    public static final DeferredHolder<SetEffect, BlackDragonArmorSetEffect> BLACK_DRAGON_ARMOR = SETS_EFFECTS.register("black_dragon_armor", BlackDragonArmorSetEffect::new);

    public static final DeferredHolder<SetEffect, InfernoArmorSetEffect> INFERNO_ARMOR = SETS_EFFECTS.register("inferno_armor", InfernoArmorSetEffect::new);

    public static final DeferredHolder<SetEffect, TwistedArmorSetEffect> TWISTED_ARMOR = SETS_EFFECTS.register("twisted_armor", TwistedArmorSetEffect::new);

    public static final DeferredHolder<SetEffect, ShadowAssassinArmorSetEffect> SHADOW_ASSASSIN_ARMOR = SETS_EFFECTS.register("shadow_assassin_armor", ShadowAssassinArmorSetEffect::new);

}
