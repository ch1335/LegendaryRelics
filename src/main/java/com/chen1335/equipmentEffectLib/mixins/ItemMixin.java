package com.chen1335.equipmentEffectLib.mixins;

import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin implements IEEItemExtension {

    @Unique
    private SetsEffectBase EE$SetsEffect = null;

    @Unique
    private List<BaseEffect> EE$ItemEffects = List.of();

    @Unique
    public SetsEffectBase EE$GetSetsEffect() {
        return EE$SetsEffect;
    }

    @Unique
    public void EE$SetSetsEffect(SetsEffectBase EE$SetsEffect) {
        this.EE$SetsEffect = EE$SetsEffect;
    }

    @Override
    public List<BaseEffect> EE$GetDefaultItemEffect() {
        return EE$ItemEffects;
    }

    @Override
    public void EE$SetDefaultItemEffect(List<BaseEffect> EE$SetsEffect) {
        this.EE$ItemEffects = EE$SetsEffect;
    }
}
