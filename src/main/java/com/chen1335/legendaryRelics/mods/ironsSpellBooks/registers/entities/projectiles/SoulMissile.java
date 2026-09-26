package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.entities.projectiles;

import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.IRSDamageTypes;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 灵魂飞弹：硬追踪——动量每 tick 直接指向目标。
 * <p>
 * 会穿墙（不做方块碰撞），只判定实体命中。视觉上目前只用粒子，不渲染模型。
 */
public class SoulMissile extends Projectile {
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(SoulMissile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(SoulMissile.class, EntityDataSerializers.INT);

    /**
     * 飞行速度（格/tick）。
     */
    private static final float SPEED = 1.2F;
    /**
     * 最长存活 tick 数。
     */
    private static final int MAX_LIFE = 100;

    private int life;

    public SoulMissile(EntityType<? extends SoulMissile> entityType, Level level) {
        super(entityType, level);
    }

    public SoulMissile(Level level, LivingEntity owner, LivingEntity target, float damage, Vec3 direction, Vec3 spawnOffset) {
        this(ISSRegisters.Entity.SOUL_MISSILE.value(), level);
        setOwner(owner);
        entityData.set(DAMAGE, damage);
        entityData.set(TARGET_ID, target.getId());

        Vec3 motion = direction.lengthSqr() < 1.0E-6 ? owner.getLookAngle() : direction.normalize();
        setDeltaMovement(motion.scale(SPEED));
        Vec3 spawnPos = owner.getEyePosition().add(spawnOffset);
        setPos(spawnPos.x, spawnPos.y, spawnPos.z);
    }

    public float getMissileDamage() {
        return entityData.get(DAMAGE);
    }

    @Nullable
    private LivingEntity getTarget() {
        if (!(level().getEntity(entityData.get(TARGET_ID)) instanceof LivingEntity living) || !living.isAlive()) {
            return null;
        }
        return living;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE, 0.0F);
        builder.define(TARGET_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnParticles();
            return;
        }

        if (++life > MAX_LIFE) {
            discard();
            return;
        }

        Vec3 motion = updateMotion();
        setDeltaMovement(motion);

        // 穿墙：只做实体命中检测，完全跳过方块检测
        Vec3 from = position();
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                level(), this, from, from.add(motion),
                getBoundingBox().expandTowards(motion).inflate(1.0), this::canHitEntity);
        if (entityHitResult != null) {
            onHitEntity(entityHitResult);
            if (isRemoved()) {
                return;
            }
        }

        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
    }

    private Vec3 updateMotion() {
        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() < 1.0E-6) {
            return motion;
        }
        Vec3 heading = motion.normalize();

        LivingEntity target = getTarget();
        if (target == null) {
            // 目标没了就保持当前方向直飞，靠 MAX_LIFE 自然消失
            return heading.scale(SPEED);
        }

        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(position());
        if (toTarget.lengthSqr() < 1.0E-6) {
            return heading.scale(SPEED);
        }

        // 硬追踪：动量每 tick 直接指向目标，不做任何转向过渡
        return toTarget.normalize().scale(SPEED);
    }

    private void spawnParticles() {
        Vec3 motion = getDeltaMovement();
        for (int i = 0; i < 3; i++) {
            level().addParticle(ParticleTypes.SCULK_SOUL,
                    getX() - motion.x * 0.15 * i + (random.nextDouble() - 0.5) * 0.15,
                    getY() - motion.y * 0.15 * i + (random.nextDouble() - 0.5) * 0.15,
                    getZ() - motion.z * 0.15 * i + (random.nextDouble() - 0.5) * 0.15,
                    0.0, 0.0, 0.0);
        }
        level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = getOwner();
        if (target == owner || target == this) {
            return false;
        }
        if (owner != null && target.isAlliedTo(owner)) {
            return false;
        }
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        target.hurt(damageSources().source(IRSDamageTypes.SOUL_MISSILE, this, getOwner()), getMissileDamage());
        discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("damage", getMissileDamage());
        compound.putInt("targetId", entityData.get(TARGET_ID));
        compound.putShort("life", (short) life);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(DAMAGE, compound.getFloat("damage"));
        entityData.set(TARGET_ID, compound.getInt("targetId"));
        life = compound.getShort("life");
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0;
    }
}
