package com.chen1335.specialEffectLib.API;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.chen1335.specialEffectLib.network.AddOrUpdateEffectPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import com.chen1335.equipmentEffectLib.utils.Cast;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpecialEffectAPI {
    public interface FinalEffectGetter<T extends SpecialMobEffect> {
        T accept(T theNew, T theOld);
    }

    public static <T extends SpecialMobEffect> void addEffectToEntity(LivingEntity target, T effect, @Nullable FinalEffectGetter<T> finalEffectGetter) {
        EntityEffectData entityEffectData = getEntityEffectData(target);
        Map<MobEffectType<?>, SpecialMobEffect> effectMap = entityEffectData.getSourceEffects(effect.getSourceEntityUUID());
        SpecialMobEffect old = effectMap.get(effect.getEffectType());
        if (finalEffectGetter != null && old != null) {
            effect = finalEffectGetter.accept(effect, Cast.cast(old));
        }
        effectMap.put(effect.getEffectType(), effect);
        effect.onAddOrUpdate(target);

        if (!target.level().isClientSide) {
            if (target instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new AddOrUpdateEffectPack(target.getId(), effect));
            } else {
                PacketDistributor.sendToPlayersTrackingEntity(target, new AddOrUpdateEffectPack(target.getId(), effect));
            }
        }
    }

    public static EntityEffectData getEntityEffectData(LivingEntity livingEntity) {
        return livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA);
    }
}
