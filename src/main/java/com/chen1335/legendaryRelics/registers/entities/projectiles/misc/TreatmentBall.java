package com.chen1335.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TreatmentBall extends Entity {
    protected static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(TreatmentBall.class, EntityDataSerializers.INT);

    public TreatmentBall(EntityType<TreatmentBall> entityType, Level level) {
        super(entityType, level);
    }

    private LivingEntity targetEntity;
    private int multiplier = 0;


    public TreatmentBall(Level level, LivingEntity targetEntity, int multiplier) {
        this(LREntityTypes.TREATMENT_BALL.value(), level);
        this.targetEntity = targetEntity;
        this.multiplier = multiplier;
        entityData.set(TARGET_ID, targetEntity.getId());
    }

    @Override
    public void tick() {
        if (targetEntity == null) {
            targetEntity = ((LivingEntity) this.level().getEntity(entityData.get(TARGET_ID)));
        }

        if (targetEntity == null) {
            this.discard();
            return;
        }

        float a = 0.8F;
        Vec3 movement = targetEntity.getEyePosition().subtract(this.getEyePosition()).normalize().multiply(a, a, a);
        this.setDeltaMovement(movement);
        Vec3 deltaMovement = getDeltaMovement();

        Vec3 newPos = position().add(deltaMovement);
        setPos(newPos);


        if (targetEntity.getEyePosition().subtract(this.getEyePosition()).lengthSqr() <= 1) {
            this.discard();
            targetEntity.heal((targetEntity.getMaxHealth() - targetEntity.getHealth()) * 0.01F * multiplier);
        }


        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.INSTANT_EFFECT, getX(), getY(), getZ(), 0, 0, 0);
        }


        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(TARGET_ID, 1);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {

    }
}
