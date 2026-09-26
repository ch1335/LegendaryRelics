package com.chen1335.legendaryRelics.mods.ironsSpellBooks.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SoulEffectInstance extends EffectInstance {
    public int coolDown = 0;

    public SoulEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        coolDown = Math.max(coolDown - 1, 0);
    }
}
