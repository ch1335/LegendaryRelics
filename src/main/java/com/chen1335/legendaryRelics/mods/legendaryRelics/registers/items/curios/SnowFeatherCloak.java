package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.item.Rarity;

import java.util.List;

/**
 * 雪羽斗篷：免疫细雪的冻结与陷入并提升移速，同时延长自己造成的减速效果。
 */
public class SnowFeatherCloak extends LRCuriosBase {
    public SnowFeatherCloak() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.TREADING_SNOW.value().create(1, EquipmentTypes.CURIO),
                LREquipmentEffectTypes.FROST_BLOOM.value().create(1, EquipmentTypes.CURIO));
    }
}
