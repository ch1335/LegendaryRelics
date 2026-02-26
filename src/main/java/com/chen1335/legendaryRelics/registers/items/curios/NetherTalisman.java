package com.chen1335.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class NetherTalisman extends LRCuriosBase {

    public NetherTalisman() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value().create(1, EquipmentType.CURIO)
        );
    }
}
