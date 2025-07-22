package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.AttributeBoostInNether;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class NetherTalisman extends LRCuriosBase {

    public NetherTalisman() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new AttributeBoostInNether(1));
    }
}
