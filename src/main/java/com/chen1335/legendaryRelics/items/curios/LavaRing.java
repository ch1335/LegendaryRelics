package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.FireDamageReduce;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class LavaRing extends LRCuriosBase {
    public LavaRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> EE$getDefaultEffects() {
        return List.of(new FireDamageReduce(1));
    }

}
