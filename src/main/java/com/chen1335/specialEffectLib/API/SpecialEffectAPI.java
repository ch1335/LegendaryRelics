package com.chen1335.specialEffectLib.API;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpecialEffectAPI {
    public interface FinalEffectGetter<T extends SpecialMobEffect> {
        T accept(T theNew, T theOld);
    }

    public static <T extends SpecialMobEffect> void addEffectToEntity(LivingEntity target, T effect, @Nullable FinalEffectGetter<T> finalEffectGetter) {
        EntityEffectData entityEffectData = target.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA);
        Map<MobEffectType<?>, SpecialMobEffect> effectMap = entityEffectData.getSourceEffects(effect.getSourceEntityUUID());
        SpecialMobEffect old = effectMap.get(effect.getEffectType());
        if (finalEffectGetter != null && old != null) {
            effect = finalEffectGetter.accept(effect, (T) old);
        }
        effectMap.put(effect.getEffectType(), effect);
    }
}
