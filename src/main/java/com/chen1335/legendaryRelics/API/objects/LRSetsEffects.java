package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSetsEffects {
    public static DeferredRegister<SetsEffectBase> SETS_EFFECTS = DeferredRegister.create(RegisterTypes.SETS_EFFECT_TYPE_KEY, LegendaryRelics.MODID);

    public static DeferredHolder<SetsEffectBase, BlackDragonArmorSetEffect> BLACK_DRAGON_ARMOR = SETS_EFFECTS.register("black_dragon_armor", BlackDragonArmorSetEffect::new);
}
