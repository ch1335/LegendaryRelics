package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.AgglomerationMaliceEffect;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

public class AgglomerationMalice extends LRCuriosBase {
    public AgglomerationMalice() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new AgglomerationMaliceEffect(1));
    }
}
