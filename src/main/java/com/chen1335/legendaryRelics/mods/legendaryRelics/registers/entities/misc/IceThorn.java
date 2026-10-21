package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 冰刺：从地下钻出、刺伤上方目标后消失的地面陷阱。
 * <p>不参与碰撞（{@code noPhysics}），伤害在完全伸出后逐帧结算，每个目标只打一次。
 */
public class IceThorn extends Entity {
    public static final float WIDTH = 0.7F;
    public static final float HEIGHT = 1.5F;
    /**
     * 钻出耗时，渲染端据此做上升动画。
     */
    public static final int EMERGE_TICKS = 6;
    /**
     * 完全伸出后停留多久再消失。
     */
    private static final int HOLD_TICKS = 6;
    private static final float DEFAULT_DAMAGE = 8.0F;

    @Nullable
    private LivingEntity owner;
    /**
     * 同一波冰刺共享的命中记录；单独生成（如 /summon）时为空，始终满伤。
     */
    @Nullable
    private Wave wave;
    private float damage = DEFAULT_DAMAGE;
    /**
     * 已命中的目标，避免同一目标逐帧重复受伤。
     */
    private final Set<UUID> hurtTargets = new HashSet<>();
    private int age;

    public IceThorn(EntityType<? extends IceThorn> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    /**
     * 在 (x, y, z) 的地面位置生成一根冰刺；y 就是冰刺底部。
     */
    public static IceThorn spawn(Level level, double x, double y, double z, @Nullable LivingEntity owner, float damage) {
        return spawn(level, x, y, z, owner, damage, null);
    }

    public static IceThorn spawn(Level level, double x, double y, double z, @Nullable LivingEntity owner,
                                 float damage, @Nullable Wave wave) {
        IceThorn thorn = new IceThorn(LREntityTypes.ICE_THORN.value(), level);
        thorn.setPos(x, y, z);
        thorn.owner = owner;
        thorn.damage = damage > 0 ? damage : DEFAULT_DAMAGE;
        thorn.wave = wave;
        level.addFreshEntity(thorn);
        return thorn;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.age = compound.getInt("Age");
        if (compound.contains("Damage")) {
            this.damage = compound.getFloat("Damage");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", this.age);
        compound.putFloat("Damage", this.damage);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (++this.age == EMERGE_TICKS) {
            // 一圈会同时出很多根，音量压低、音调随机，避免叠成噪音
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundType.GLASS.getPlaceSound(), SoundSource.BLOCKS,
                    0.35F, 1.2F + this.random.nextFloat() * 0.4F);
        }
        if (this.age >= EMERGE_TICKS) {
            this.hurtAbove();
        }
        if (this.age >= EMERGE_TICKS + HOLD_TICKS) {
            this.level().broadcastEntityEvent(this, (byte) 1);
            this.discard();
        }
    }

    private void hurtAbove() {
        AABB hurtBox = new AABB(
                this.getX() - WIDTH / 2.0, this.getY(), this.getZ() - WIDTH / 2.0,
                this.getX() + WIDTH / 2.0, this.getY() + HEIGHT, this.getZ() + WIDTH / 2.0);
        DamageSource source = this.owner == null
                ? this.level().damageSources().source(LRDamageTypes.ICE_THORN, this)
                : this.level().damageSources().source(LRDamageTypes.ICE_THORN, this, this.owner);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, hurtBox, Entity::isAlive)) {
            if (target == this.owner || !this.hurtTargets.add(target.getUUID())) {
                continue;
            }
            // 同一波里，对同一个目标造成伤害的冰刺从第二根开始只结算 15%
            float multiplier = this.wave == null ? 1.0F : this.wave.damageMultiplier(target.getUUID());
            if (target.hurt(source, this.damage * multiplier)) {
                if (this.wave != null) {
                    // 只有真正造成伤害的冰刺才占用满伤名额，免疫/被挡下的不算
                    this.wave.recordHit(target.getUUID());
                }
                // 同一目标只顶一次，否则几十根冰刺叠加会把人顶飞上天
                if (multiplier >= 1.0F) {
                    target.push(0.0, 0.25, 0.0);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 1) {
            for (int i = 0; i < 12; i++) {
                this.level().addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                        this.getX() + (this.random.nextDouble() - 0.5) * WIDTH,
                        this.getY() + this.random.nextDouble() * HEIGHT,
                        this.getZ() + (this.random.nextDouble() - 0.5) * WIDTH,
                        0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    /**
     * 一次冰刺波共享的命中记录：同一波里，对同一个目标造成伤害的第一根冰刺满伤，
     * 后续命中的冰刺只按 {@link #REPEAT_MULTIPLIER} 结算。
     */
    public static class Wave {
        /**
         * 对同一目标第二根及之后冰刺的伤害倍率。
         */
        public static final float REPEAT_MULTIPLIER = 0.15F;

        private final Map<UUID, Integer> hitCounts = new HashMap<>();

        /**
         * 该目标当前已受伤次数对应的倍率：0 次是满伤，之后都是 15%。
         */
        public float damageMultiplier(UUID target) {
            return hitCounts.getOrDefault(target, 0) == 0 ? 1.0F : REPEAT_MULTIPLIER;
        }

        /**
         * 真正造成伤害后登记，避免免疫/被挡下的冰刺占掉满伤名额。
         */
        public void recordHit(UUID target) {
            hitCounts.merge(target, 1, Integer::sum);
        }
    }
}
