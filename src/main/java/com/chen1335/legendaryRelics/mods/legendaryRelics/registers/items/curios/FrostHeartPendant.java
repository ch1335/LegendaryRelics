package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

/**
 * 霜心项链：受到近战伤害时给攻击者减速，并在寒冷群系中提升大部分属性。
 */
public class FrostHeartPendant extends LRCuriosBase {
    public FrostHeartPendant() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.FROST_HEART.value().create(1, EquipmentTypes.CURIO),
                LREquipmentEffectTypes.COLD_DOMAIN.value().create(1, EquipmentTypes.CURIO));
    }
}
