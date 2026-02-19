package com.chen1335.equipmentEffectLib.MixinsAPI;

import com.chen1335.equipmentEffectLib.common.SetEffectHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IEEItemExtension {
    @Nullable
    SetEffectHolder EE$GetSetsEffect();

    void EE$SetSetsEffect(SetEffectHolder EE$SetsEffect);

    List<BaseEffect> EE$GetDefaultItemEffect();

    void EE$SetDefaultItemEffect(List<BaseEffect> EE$SetsEffect);
}
