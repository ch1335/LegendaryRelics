package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityDataSerializers;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.LRProjectile;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons.FrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * 霜锥投掷后的实体。
 *
 * <p>物品本身不会被消耗，这个实体只负责飞行与命中结算：命中生物后立即消失，
 * 命中方块则像三叉戟一样插在地上直到自然过期。
 *
 * <p>因为 {@link LRProjectile} 继承自 {@code Projectile} 而非 {@code AbstractArrow}，
 * 它压根没有拾取机制，投出去的武器不会变成掉落物。
 */
public class ThrownFrostShard extends LRProjectile {

    private static final EntityDataAccessor<Item> ITEM = SynchedEntityData.defineId(ThrownFrostShard.class, LREntityDataSerializers.ITEM.get());

    /** 破碎时的实体事件 id，客户端据此生成物品碎屑粒子。 */
    private static final byte SHATTER_EVENT = 3;

    /** 命中实体后向左右溅射的冰锥数量。 */
    private static final int SPLASH_COUNT = 5;

    /** 相邻两枚冰锥之间的夹角（度）。 */
    private static final float SPLASH_ANGLE_STEP = 10.0F;

    /** 溅射出的冰锥初速。 */
    private static final float SPLASH_SPEED = 1.5F;

    private ItemStack renderItem = null;

    public ThrownFrostShard(EntityType<? extends LRProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownFrostShard(LivingEntity owner, Level level, @Nullable ItemStack firedFromWeapon) {
        super(LREntityTypes.THROWN_FROST_SHARD.value(), owner, level, firedFromWeapon);
        if (firedFromWeapon != null) {
            entityData.set(ITEM, firedFromWeapon.getItem());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ITEM, Items.AIR);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (renderItem != null && !renderItem.isEmpty()) {
            compound.putString("renderItem", BuiltInRegistries.ITEM.getKey(renderItem.getItem()).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("renderItem")) {
            BuiltInRegistries.ITEM.getHolder(ResourceLocation.parse(compound.getString("renderItem"))).ifPresent(itemReference -> {
                entityData.set(ITEM, itemReference.value());
            });
        }
    }

    public ItemStack getRenderItem() {
        if (renderItem == null) {
            renderItem = entityData.get(ITEM).getDefaultInstance();
        }
        return renderItem;
    }

    @Override
    public void tick() {
        super.tick();
        // 飞行途中在客户端留下雪花轨迹；插进方块或已经命中移除后就不再生成
        if (this.level().isClientSide && !this.inGround && !this.isRemoved()) {
            this.spawnTrailParticles();
        }
    }

    /**
     * 沿飞行方向拖出一串雪花粒子，带一点随机抖动。
     */
    private void spawnTrailParticles() {
        Vec3 motion = this.getDeltaMovement();
        for (int i = 0; i < 2; i++) {
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                    this.getX() - motion.x * 0.15 * i + (this.random.nextDouble() - 0.5) * 0.15,
                    this.getY() - motion.y * 0.15 * i + (this.random.nextDouble() - 0.5) * 0.15,
                    this.getZ() - motion.z * 0.15 * i + (this.random.nextDouble() - 0.5) * 0.15,
                    0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        // 溅射方向取命中瞬间的飞行方向，必须在下面改动 deltaMovement 之前取出
        Vec3 hitDirection = this.getDeltaMovement();
        DamageSource source = this.level().damageSources().source(LRDamageTypes.FROST_SHARD, this, this.getOwner());
        float damage = getBaseDamage();
        if (!level().isClientSide && getWeaponItem() != null) {
            damage = EnchantmentHelper.modifyDamage((ServerLevel) level(), this.getWeaponItem(), entity, source, damage);
        }
        if (entity.hurt(source, damage)) {
            if (!level().isClientSide && getWeaponItem() != null) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) level(), entity, source, getWeaponItem());
            }
            // 投掷物命中：给目标挂减速
            if (!level().isClientSide && entity instanceof LivingEntity livingTarget) {
                CalculatorArg arg = buildArg();
                Slowness.apply(livingTarget, this.getOwner(),
                        FrostShard.THROWN_SLOWNESS.getValue(arg),
                        (int) (FrostShard.SLOWNESS_TIME.getValue(arg) * 20));
            }
        } else {
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
        }

        this.splashIcicles(hitDirection, entity);
        this.shatter();
    }

    /**
     * 计算溅射伤害与减速比例用的参数；拿不到投掷者或武器时退回空参数。
     */
    private CalculatorArg buildArg() {
        LivingEntity thrower = this.getOwner() instanceof LivingEntity living ? living : null;
        ItemStack weapon = this.getWeaponItem();
        return thrower != null && weapon != null
                ? CalculatorArg.simpleArg(thrower, weapon)
                : CalculatorArg.emptyArg();
    }

    /**
     * 沿命中方向向左右溅射出若干冰锥。
     *
     * <p>伤害算法由 {@link FrostShard#ICICLE_DAMAGE} 提供，冰锥实体只承载算好的数值。
     * 方向、位置与伤害都以命中瞬间为准，所以要在 deltaMovement 被改动之前调用。
     *
     * @param excludedTarget 霜锥本体已经命中的目标，冰锥跳过它；冰锥之间互不排斥，可以打同一个目标
     */
    private void splashIcicles(Vec3 hitDirection, Entity excludedTarget) {
        if (this.level().isClientSide || hitDirection.lengthSqr() < 1.0E-4) {
            return;
        }

        CalculatorArg arg = buildArg();
        float icicleDamage = FrostShard.ICICLE_DAMAGE.getValue(arg);

        Vec3 direction = hitDirection.normalize();
        for (int i = 0; i < SPLASH_COUNT; i++) {
            // 以命中方向为中心，左右各展开 (SPLASH_COUNT - 1) / 2 个角度间隔
            double radians = Math.toRadians((i - (SPLASH_COUNT - 1) / 2.0F) * SPLASH_ANGLE_STEP);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            // 只旋转水平分量、y 保持不变，形成左右扇形
            Vec3 spread = new Vec3(
                    direction.x * cos - direction.z * sin,
                    direction.y,
                    direction.x * sin + direction.z * cos);

            LivingEntity thrower = this.getOwner() instanceof LivingEntity living ? living : null;
            Icicle icicle = new Icicle(thrower, this.level(), this.getWeaponItem());
            // 不让冰锥打中霜锥本体已经命中的目标；冰锥之间互不排斥，可以打同一个目标
            icicle.setExcludedTarget(excludedTarget);
            // 生成点沿飞行方向前移一点，避免冰锥直接嵌在命中目标的模型里
            icicle.setPos(
                    this.getX() + direction.x * 0.3D,
                    this.getY() + direction.y * 0.3D,
                    this.getZ() + direction.z * 0.3D);
            icicle.shoot(spread.x, spread.y, spread.z, 1.5F, 0.0F);
            icicle.setBaseDamage(icicleDamage);
            this.level().addFreshEntity(icicle);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.shatter();
    }

    /**
     * 命中后破碎：服务端播放碎冰音效并广播事件，客户端据事件生成物品碎屑，最后移除自身。
     *
     * <p>{@code Level.addParticle} 在服务端是空实现，所以粒子只能由客户端生成；
     * 这里沿用原版投掷物（雪球、鸡蛋）的实体事件写法，保证附近玩家都能看到碎屑。
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
            ItemStack renderItem = this.getRenderItem();
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
}
