package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class HolyCloak extends LRCuriosBase {
    public HolyCloak() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.HOLY_BARRIER.value().create(1, EquipmentTypes.CURIO));
    }
}
