package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.Redemption;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LREquipmentEffectTypes {
    public static DeferredRegister<EffectType<?>> EQUIPMENT_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.EQUIPMENT_EFFECT_TYPE, LegendaryRelics.MODID);

    public static DeferredHolder<EffectType<?>, EffectType<Redemption>> REDEMPTION = EQUIPMENT_EFFECT_TYPES.register("redemption", () -> new EffectType<>(Redemption::new, EntityEquipmentEffectData.EquipmentType.CURIO));

}
