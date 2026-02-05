package com.chen1335.legendaryRelics.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FlyingReaper extends AbstractArrow {
    protected static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(FlyingReaper.class, EntityDataSerializers.FLOAT);

    public ItemStack renderStack = LRItems.REAPER.get().getDefaultInstance();

    public float currentRot = 0;

    public float damageMultiplier = 1F;

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    public boolean inGround() {
        return inGround;
    }

    public FlyingReaper(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public FlyingReaper(Level level, LivingEntity owner, ItemStack weapon, float damage) {
        this(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), level, weapon, weapon);
        setOwner(owner);
        setBaseDamage(damage);
    }

    public FlyingReaper(double x, double y, double z, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(LREntityTypes.FLYING_REAPER.value(), x, y, z, level, pickupItemStack, firedFromWeapon);
    }

    @Override
    public void shootFromRotation(@NotNull Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * (float) (Math.PI / 180.0)) * Mth.cos(x * (float) (Math.PI / 180.0));
        float f1 = -Mth.sin((x + z) * (float) (Math.PI / 180.0));
        float f2 = Mth.cos(y * (float) (Math.PI / 180.0)) * Mth.cos(x * (float) (Math.PI / 180.0));
        this.shoot(f, f1, f2, velocity, inaccuracy);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("DamageMultiplier", damageMultiplier);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        damageMultiplier = compound.getFloat("DamageMultiplier");
    }

    @Override
    public void tick() {
        super.tick();
        this.getEntityData().set(SPEED, (float) this.getDeltaMovement().length());

        Vec3 vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d1 = vec3.z;
        this.setYRot((float) (Mth.atan2(d5, d1) * 180.0F / (float) Math.PI));

        if (inGroundTime >= 30 * 20) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof Player player && player == getOwner()) {
            player.swing(InteractionHand.MAIN_HAND, true);
            this.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1F);
            this.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            inGround = false;
            return;
        }
        float f = (float) getBaseDamage() * damageMultiplier;


        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().source(LRDamageTypes.FLYING_REAPER, this, entity1 == null ? this : entity1);

        if (getOwner() instanceof ServerPlayer serverPlayer && this.getWeaponItem() != null) {
            f += EnchantmentHelper.modifyDamage((ServerLevel) serverPlayer.level(), this.getWeaponItem(), entity, damagesource, 0);
        }

        if (entity.hurt(damagesource, f) && getWeaponItem() != null) {
            if (!entity.level().isClientSide) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) entity.level(), entity, damagesource, getWeaponItem());
            }
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            damageMultiplier = damageMultiplier - 0.2F;
            if (damageMultiplier <= 0.1) {
                this.discard();
                this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);
            }
        }
        Vec3 normalize = this.getDeltaMovement().normalize();
        double x = normalize.x;
        double z = normalize.z;
        this.setPos(entity.getX(), entity.getEyeY() - 0.1F, entity.getZ());
        this.setDeltaMovement(x * -0.5, 0.2, z * -0.5);
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        Vec3 deltaMovement = getDeltaMovement();
        float i = 1;
        setSoundEvent(SoundEvents.TRIDENT_HIT_GROUND);
        super.onHitBlock(result);

        Direction direction = result.getDirection();
        if (direction == Direction.DOWN) {
            setDeltaMovement(deltaMovement.x, -deltaMovement.y * i, deltaMovement.z);
            inGround = false;
        } else if (direction == Direction.SOUTH || direction == Direction.NORTH) {
            setDeltaMovement(deltaMovement.x, deltaMovement.y, -deltaMovement.z * i);
            inGround = false;
        } else if (direction == Direction.EAST || direction == Direction.WEST) {
            setDeltaMovement(-deltaMovement.x, deltaMovement.y, deltaMovement.z * i);
            inGround = false;
        }
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void setOnGround(boolean onGround) {
        super.setOnGround(onGround);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPEED, 0F);
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return renderStack;
    }

    public ItemStack getRenderStack() {
        return renderStack;
    }

    @Override
    public boolean isPickable() {
        return false;
    }


    @Override
    public void playerTouch(@NotNull Player player) {
        if (player == getOwner() && inGround() && player.getData(LRAttachmentTypes.ENTITY_DATA.get()).reaperPickCooldown == 0) {
            player.swing(InteractionHand.MAIN_HAND, true);
            this.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1F);
            this.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            player.getData(LRAttachmentTypes.ENTITY_DATA.get()).reaperPickCooldown = 4;
            inGround = false;
        }
    }

}
