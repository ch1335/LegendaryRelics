package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * 环绕玩家的烈焰飞轮。位置由服务端权威计算，Minecraft 实体同步系统会把坐标发给追踪的客户端；
 * 客户端只负责渲染物品模型与自转动画。
 * <p>
 * 三态状态机：
 * <ul>
 *     <li>{@link #STATE_ORBITING} 环绕：跟随 owner 做圆周运动。</li>
 *     <li>{@link #STATE_ATTACKING} 突进：飞向目标，命中后造成伤害并转入返回。</li>
 *     <li>{@link #STATE_RETURNING} 返回：飞回环绕点，到位后转回环绕。</li>
 * </ul>
 * 环绕索引/数量、伤害与攻击间隔由 {@code OrbitingFireChakramsEffect} 每 tick 下发。
 */
public class OrbitingFireChakram extends Entity {

    public static final int STATE_ORBITING = 0;
    public static final int STATE_ATTACKING = 1;
    public static final int STATE_RETURNING = 2;

    private static final float ORBIT_RADIUS = 1.6F;
    private static final float ORBIT_Y_OFFSET = 1.0F;
    private static final float ORBIT_SPEED_DEG = 6.0F;
    private static final float FLY_SPEED = 1.1F;
    private static final float REACH_DISTANCE = 1.2F;

    // 仅同步客户端重建平滑轨道所需的低频元数据；自转和逐帧位置不使用 entityData
    private static final EntityDataAccessor<Integer> OWNER_ID =
            SynchedEntityData.defineId(OrbitingFireChakram.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORBIT_INDEX =
            SynchedEntityData.defineId(OrbitingFireChakram.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORBIT_COUNT =
            SynchedEntityData.defineId(OrbitingFireChakram.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(OrbitingFireChakram.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(OrbitingFireChakram.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity owner;
    private int orbitIndex = 0;
    private int orbitCount = 1;
    private float damage = 0.0F;

    /**
     * 该飞轮独立的攻击间隔与剩余冷却；各飞轮相位错开以实现穿插攻击。
     */
    private int attackInterval = 40;
    private int attackCooldown = 0;

    private int state = STATE_ORBITING;
    @Nullable
    private LivingEntity target;

    /**
     * 客户端独立的纯视觉位置，避免本地预测与服务端实体坐标同步互相覆盖。
     */
    @Nullable
    private Vec3 clientVisualPosition;
    @Nullable
    private Vec3 clientPreviousVisualPosition;

    public OrbitingFireChakram(EntityType<? extends OrbitingFireChakram> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public OrbitingFireChakram(Level level, LivingEntity owner) {
        this(LREntityTypes.ORBITING_FIRE_CHAKRAM.value(), level);
        setOwner(owner);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        if (!level().isClientSide) {
            entityData.set(OWNER_ID, owner == null ? -1 : owner.getId());
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (owner == null && level().isClientSide) {
            Entity entity = level().getEntity(entityData.get(OWNER_ID));
            if (entity instanceof LivingEntity living) {
                owner = living;
            }
        }
        return owner;
    }

    /**
     * 由效果调度器在每个服务端 tick 更新，确保飞轮数量增减时环绕分布均匀，并同步最新数值。
     */
    public void updateOrbit(int index, int count, float damage, int attackInterval) {
        this.orbitIndex = index;
        this.orbitCount = Math.max(1, count);
        this.damage = damage;
        this.attackInterval = Math.max(1, attackInterval);
        entityData.set(ORBIT_INDEX, this.orbitIndex);
        entityData.set(ORBIT_COUNT, this.orbitCount);
    }

    /**
     * 设置初始攻击冷却，用于让各飞轮错开攻击相位。
     */
    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = Math.max(0, cooldown);
    }

    private void setState(int state) {
        this.state = state;
        entityData.set(STATE, state);
    }

    public int getMotionState() {
        return level().isClientSide ? entityData.get(STATE) : state;
    }

    /**
     * 环绕状态下供渲染器使用：基于 owner 的 partialTick 插值位置本地重建平滑轨道。
     */
    public Vec3 getRenderOrbitPosition(float partialTick) {
        LivingEntity o = getOwner();
        if (o == null) {
            return getPosition(partialTick);
        }
        int index = level().isClientSide ? entityData.get(ORBIT_INDEX) : orbitIndex;
        int count = Math.max(1, level().isClientSide ? entityData.get(ORBIT_COUNT) : orbitCount);
        double ownerX = o.xOld + (o.getX() - o.xOld) * partialTick;
        double ownerY = o.yOld + (o.getY() - o.yOld) * partialTick;
        double ownerZ = o.zOld + (o.getZ() - o.zOld) * partialTick;
        double tick = level().getGameTime() - 1.0D + partialTick;
        double angle = Math.toRadians(tick * ORBIT_SPEED_DEG + index * (360.0F / count));
        return new Vec3(
                ownerX + Math.cos(angle) * ORBIT_RADIUS,
                ownerY + ORBIT_Y_OFFSET,
                ownerZ + Math.sin(angle) * ORBIT_RADIUS
        );
    }

    /**
     * 渲染器使用的客户端视觉位置；不受服务端网络位置修正直接覆盖。
     */
    public Vec3 getClientVisualPosition(float partialTick) {
        if (getMotionState() == STATE_ORBITING) {
            return getRenderOrbitPosition(partialTick);
        }
        if (clientVisualPosition == null || clientPreviousVisualPosition == null) {
            return getPosition(partialTick);
        }
        return clientPreviousVisualPosition.lerp(clientVisualPosition, partialTick);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity currentOwner = getOwner();
        if (currentOwner == null || !currentOwner.isAlive() || currentOwner.isRemoved()) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        if (level().isClientSide) {
            tickClientMovement();
            return;
        }

        switch (state) {
            case STATE_ATTACKING -> tickAttacking();
            case STATE_RETURNING -> tickReturning();
            default -> tickOrbiting();
        }
    }

    /**
     * 客户端只推进纯视觉轨迹，不改写实体坐标，不寻敌、不计冷却、不造成伤害。
     */
    private void tickClientMovement() {
        if (clientVisualPosition == null) {
            clientVisualPosition = position();
        }
        clientPreviousVisualPosition = clientVisualPosition;

        switch (entityData.get(STATE)) {
            case STATE_ATTACKING -> {
                Entity entity = level().getEntity(entityData.get(TARGET_ID));
                if (entity instanceof LivingEntity living && living.isAlive()) {
                    clientVisualPosition = moveVisualTowards(clientVisualPosition, living.getPosition(0).add(0, living.getDimensions(living.getPose()).height() / 2, 0));
                }
            }
            case STATE_RETURNING -> clientVisualPosition = moveVisualTowards(clientVisualPosition, orbitPosition());
            default -> clientVisualPosition = orbitPosition();
        }
    }

    private Vec3 moveVisualTowards(Vec3 from, Vec3 destination) {
        Vec3 dir = destination.subtract(from);
        if (dir.lengthSqr() <= FLY_SPEED * FLY_SPEED) {
            return destination;
        }
        return from.add(dir.normalize().scale(FLY_SPEED));
    }

    /**
     * 环绕位置：以世界游戏时间为相位基准，两端算出来的轨道一致。
     */
    private Vec3 orbitPosition() {
        LivingEntity o = getOwner();
        if (o == null) {
            return position();
        }
        int index = level().isClientSide ? entityData.get(ORBIT_INDEX) : orbitIndex;
        int count = Math.max(1, level().isClientSide ? entityData.get(ORBIT_COUNT) : orbitCount);
        double angle = Math.toRadians(level().getGameTime() * ORBIT_SPEED_DEG + index * (360.0F / count));
        return new Vec3(
                o.getX() + Math.cos(angle) * ORBIT_RADIUS,
                o.getY() + ORBIT_Y_OFFSET,
                o.getZ() + Math.sin(angle) * ORBIT_RADIUS
        );
    }

    private void tickOrbiting() {
        Vec3 pos = orbitPosition();
        moveTo(pos.x, pos.y, pos.z);

        // 独立冷却：归零后尝试攻击，成功则重置为自身攻击间隔；找不到目标就下一 tick 再试
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }
        if (tryStartAttack()) {
            attackCooldown = attackInterval;
        }
    }

    private void tickAttacking() {
        if (target == null || !target.isAlive() || target.isRemoved()) {
            target = null;
            entityData.set(TARGET_ID, -1);
            setState(STATE_RETURNING);
            return;
        }
        Vec3 aim = target.getEyePosition();
        Vec3 dir = aim.subtract(position());
        if (dir.length() <= REACH_DISTANCE) {
            dealDamage(target);
            target = null;
            entityData.set(TARGET_ID, -1);
            setState(STATE_RETURNING);
            return;
        }
        Vec3 next = position().add(dir.normalize().scale(FLY_SPEED));
        moveTo(next.x, next.y, next.z);
    }

    private void tickReturning() {
        Vec3 home = orbitPosition();
        Vec3 dir = home.subtract(position());
        if (dir.length() <= FLY_SPEED) {
            setState(STATE_ORBITING);
            tickOrbiting();
            return;
        }
        Vec3 next = position().add(dir.normalize().scale(FLY_SPEED));
        moveTo(next.x, next.y, next.z);
    }

    private boolean tryStartAttack() {
        if (owner == null) {
            return false;
        }
        LivingEntity found = findTarget();
        if (found == null) {
            return false;
        }
        this.target = found;
        entityData.set(TARGET_ID, found.getId());
        setState(STATE_ATTACKING);
        return true;
    }

    @Nullable
    private LivingEntity findTarget() {
        if (owner == null) {
            return null;
        }
        AABB box = owner.getBoundingBox().inflate(12.0D);
        List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class, box,this::isAvailableTarget);
        return candidates.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .orElse(null);
    }

    private boolean isAvailableTarget(LivingEntity target) {
        if (owner == null) {
            return false;
        }

        boolean canAttack = target != owner
                && target instanceof Enemy
                && target.isAlive()
                && !target.isSpectator()
                && !target.isAlliedTo(owner)
                && owner.canAttack(target);
        if (owner instanceof Player player) {
            return canAttack || LRPlayerData.getData(player).isAttacking(target);
        }
        return canAttack;
    }

    private void dealDamage(LivingEntity victim) {
        if (owner == null || damage <= 0.0F) {
            return;
        }
        DamageSource source = level().damageSources().source(LRDamageTypes.ORBITING_FIRE_CHAKRAM, owner);
        victim.hurt(source, damage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(OWNER_ID, -1);
        builder.define(ORBIT_INDEX, 0);
        builder.define(ORBIT_COUNT, 1);
        builder.define(STATE, STATE_ORBITING);
        builder.define(TARGET_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        // 临时实体，不持久化；由效果在重载后重新生成
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
