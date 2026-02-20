package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class TheOreCollectorsRing extends LRCuriosBase {
    public TheOreCollectorsRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value().create(1, EquipmentType.CURIO));
    }

}
