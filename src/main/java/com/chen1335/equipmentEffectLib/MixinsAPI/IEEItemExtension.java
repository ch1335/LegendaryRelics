package com.chen1335.equipmentEffectLib.MixinsAPI;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;

import java.util.List;

public interface IEEItemExtension {
    SetsEffectBase EE$GetSetsEffect();

    void EE$SetSetsEffect(SetsEffectBase EE$SetsEffect);

    List<BaseEffect> EE$GetItemEffect();

    void EE$SetItemEffect(List<BaseEffect> EE$SetsEffect);
}
