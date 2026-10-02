package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityDataSerializers;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.LRProjectile;
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
        DamageSource source = this.level().damageSources().source(LRDamageTypes.FROST_SHARD, this, this.getOwner());
        float damage = getBaseDamage();
        if (!level().isClientSide && getWeaponItem() != null) {
            damage = EnchantmentHelper.modifyDamage((ServerLevel) level(), this.getWeaponItem(), entity, source, damage);
        }
        if (entity.hurt(source, damage)) {
            if (!level().isClientSide && getWeaponItem() != null) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) level(), entity, source, getWeaponItem());
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
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                        this.getX(), this.getY(), this.getZ(),
                        ((double) this.random.nextFloat() - 0.5),
                        ((double) this.random.nextFloat() - 0.5),
                        ((double) this.random.nextFloat() - 0.5));
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
}
