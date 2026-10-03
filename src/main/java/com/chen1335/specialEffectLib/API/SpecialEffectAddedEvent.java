package com.chen1335.specialEffectLib.API;

import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 特殊效果被添加到实体上时触发，对应原版的 {@code MobEffectEvent.Added}。
 *
 * <p>触发点在 {@link SpecialEffectAPI#addEffectToEntity}：效果已经写进实体数据、但还没调用
 * {@code onAddOrUpdate}，所以监听者还能在效果真正生效前调整它（例如延长持续时间）。
 *
 * <p>不可取消：特殊效果已经进入实体数据，拒绝它应当用 {@link SpecialMobEffect#canApplyTo}。
 */
public class SpecialEffectAddedEvent extends LivingEvent {

    private final SpecialMobEffect specialEffect;

    public SpecialEffectAddedEvent(LivingEntity entity, SpecialMobEffect specialEffect) {
        super(entity);
        this.specialEffect = specialEffect;
    }

    /** 刚被添加的特殊效果实例，可自行转型后修改（如 {@code TimeLimitEffect#setTimeLeft}）。 */
    public SpecialMobEffect getSpecialEffect() {
        return specialEffect;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return super.getEntity();
    }
}
