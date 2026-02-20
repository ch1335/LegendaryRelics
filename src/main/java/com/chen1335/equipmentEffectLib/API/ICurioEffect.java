package com.chen1335.equipmentEffectLib.API;

import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public interface ICurioEffect extends ITickAbleEffect {
    default void modifyCurioAttribute(CurioAttributeModifierEvent event) {

    }
}
