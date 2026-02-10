package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.armorSetEffect.TwistedArmorSetEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSetsEffects {
    public static DeferredRegister<SetEffect> SETS_EFFECTS = DeferredRegister.create(EERegisterTypes.SETS_EFFECT_TYPE_KEY, LegendaryRelics.MODID);

    public static DeferredHolder<SetEffect, BlackDragonArmorSetEffect> BLACK_DRAGON_ARMOR = SETS_EFFECTS.register("black_dragon_armor", BlackDragonArmorSetEffect::new);

    public static DeferredHolder<SetEffect, InfernoArmorSetEffect> INFERNO_ARMOR = SETS_EFFECTS.register("inferno_armor", InfernoArmorSetEffect::new);

    public static DeferredHolder<SetEffect, TwistedArmorSetEffect> TWISTED_ARMOR = SETS_EFFECTS.register("twisted_armor", TwistedArmorSetEffect::new);

}
