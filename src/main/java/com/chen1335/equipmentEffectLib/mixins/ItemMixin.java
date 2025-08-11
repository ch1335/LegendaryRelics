package com.chen1335.equipmentEffectLib.mixins;

import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemMixin;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements IEEItemMixin {

    @Unique private SetsEffectBase EE$SetsEffect = null;

    @Unique
    public SetsEffectBase EE$GetSetsEffect() {
        return EE$SetsEffect;
    }

    @Unique
    public void EE$SetSetsEffect(SetsEffectBase EE$SetsEffect) {
        this.EE$SetsEffect = EE$SetsEffect;
    }
}
