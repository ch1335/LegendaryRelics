package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.LRProjectile;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons.FrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

/**
 * 冰锥：受重力影响的投射物，命中后碎裂。
 *
 * <p>重力来自 {@link LRProjectile#getDefaultGravity()}（0.05），与骸骨飞刀一致，
 * 所以这里不需要额外处理抛物线。
 */
public class Icicle extends LRProjectile {

    /** 碎裂时的实体事件 id，客户端据此生成碎冰粒子。 */
    private static final byte SHATTER_EVENT = 3;

    /** 不允许命中的实体 id，-1 表示不限制。用同步数据保存，保证客户端也跳过同一个目标。 */
    private static final EntityDataAccessor<Integer> EXCLUDED_TARGET =
            SynchedEntityData.defineId(Icicle.class, EntityDataSerializers.INT);

    public Icicle(EntityType<? extends LRProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public Icicle(LivingEntity owner, Level level, @Nullable ItemStack firedFromWeapon) {
        super(LREntityTypes.ICICLE.value(), owner, level, firedFromWeapon);
    }

    /**
     * 设置不允许命中的实体（例如霜锥本体已经打中的那个目标）；传 {@code null} 取消限制。
     *
     * <p>必须在 {@code addFreshEntity} 之前调用，这样客户端会在生成实体时就拿到这个 id。
     */
    public void setExcludedTarget(@Nullable Entity target) {
        this.entityData.set(EXCLUDED_TARGET, target == null ? -1 : target.getId());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EXCLUDED_TARGET, -1);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && target.getId() != this.entityData.get(EXCLUDED_TARGET);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ExcludedTarget", this.entityData.get(EXCLUDED_TARGET));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ExcludedTarget")) {
            this.entityData.set(EXCLUDED_TARGET, compound.getInt("ExcludedTarget"));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        DamageSource source = this.level().damageSources().source(LRDamageTypes.ICICLE, this, this.getOwner());
        float damage = getBaseDamage();
        if (!level().isClientSide && getWeaponItem() != null) {
            damage = EnchantmentHelper.modifyDamage((ServerLevel) level(), this.getWeaponItem(), entity, source, damage);
        }
        if (entity.hurt(source, damage)) {
            if (!level().isClientSide && getWeaponItem() != null) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) level(), entity, source, getWeaponItem());
            }
            // 命中：给目标挂减速
            if (!level().isClientSide && entity instanceof LivingEntity livingTarget) {
                LivingEntity thrower = this.getOwner() instanceof LivingEntity living ? living : null;
                ItemStack weapon = getWeaponItem();
                CalculatorArg arg = thrower != null && weapon != null
                        ? CalculatorArg.simpleArg(thrower, weapon)
                        : CalculatorArg.emptyArg();
                Slowness.apply(livingTarget, this.getOwner(),
                        FrostShard.ICICLE_SLOWNESS.getValue(arg),
                        (int) (FrostShard.SLOWNESS_TIME.getValue(arg) * 20));
            }
        } else {
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
        }

        this.shatter();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.shatter();
    }

    /**
     * 命中后碎裂：服务端播放碎冰音效并广播事件，客户端据事件生成冰块碎屑，最后移除自身。
     *
     * <p>{@code Level.addParticle} 在服务端是空实现，所以粒子只能由客户端生成；
     * 这里沿用原版投掷物（雪球、鸡蛋）的实体事件写法。
     */
    private void shatter() {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, SHATTER_EVENT);
            this.playSound(SoundType.GLASS.getBreakSound(), 1.0F, 1.2F);
        }
        this.discard();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == SHATTER_EVENT) {
            // 冰锥没有对应的物品，所以用冰块粒子来表现"碎成冰渣"，参数与霜锥投掷物保持一致
            for (int i = 0; i < 16; i++) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                        this.getX(), this.getY(), this.getZ(),
                        ((double) this.random.nextFloat()),
                        ((double) this.random.nextFloat()),
                        ((double) this.random.nextFloat()));
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public int maxLife() {
        return 600;
    }
}
