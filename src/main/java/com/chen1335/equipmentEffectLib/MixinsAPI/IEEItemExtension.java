package com.chen1335.equipmentEffectLib.MixinsAPI;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;

import java.util.List;

public interface IEEItemExtension {
    SetEffect EE$GetSetsEffect();

    void EE$SetSetsEffect(SetEffect EE$SetsEffect);

    List<BaseEffect> EE$GetDefaultItemEffect();

    void EE$SetDefaultItemEffect(List<BaseEffect> EE$SetsEffect);
}
