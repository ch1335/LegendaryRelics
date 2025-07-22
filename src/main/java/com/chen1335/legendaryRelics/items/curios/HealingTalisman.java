package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.HealIncreaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.HealPerSecondEffect;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class HealingTalisman extends LRCuriosBase {
    public HealingTalisman() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> EE$getDefaultEffects() {
        return List.of(new HealIncreaseEffect(1), new HealPerSecondEffect(1));
    }

}
