package com.chen1335.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class LavaRing extends LRCuriosBase {
    public LavaRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.FIRE_DAMAGE_REDUCE.value().create(1, EquipmentTypes.CURIO));
    }

}
