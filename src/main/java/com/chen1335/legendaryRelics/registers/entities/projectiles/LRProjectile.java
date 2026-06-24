package com.chen1335.legendaryRelics.registers.entities.projectiles;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;

public class LRProjectile extends Projectile {
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(LRProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(LRProjectile.class, EntityDataSerializers.BYTE);
    @Nullable
    private BlockState lastState;
    public boolean inGround;
    protected int inGroundTime;
    public int shakeTime;
    private int life;
    private float baseDamage = 2F;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    @Nullable
    public IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    public List<Entity> piercedAndKilledEntities;
    private ItemStack firedFromWeapon = ItemStack.EMPTY;

    protected LRProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public LRProjectile(EntityType<? extends Projectile> entityType, LivingEntity owner, Level level, @Nullable ItemStack firedFromWeapon) {
        this(entityType, level);
        setOwner(owner);
        this.firedFromWeapon = firedFromWeapon;
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }

        d0 *= 64.0 * getViewScale();
        return distance < d0 * d0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PIERCE_LEVEL, (byte) 0);
        builder.define(ID_FLAGS, (byte) 0);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.life = 0;
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        this.life = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 180.0F / (float) Math.PI));
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * 180.0F / (float) Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            this.shakeTime--;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (this.inGround && !flag) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level().isClientSide) {
                this.tickDespawn();
            }

            this.inGroundTime++;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                vec33 = hitresult.getLocation();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag) {
                    if (EventHooks.onProjectileImpact(this, hitresult))
                        break;
                    ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(hitresult);
                    this.hasImpulse = true;
                    if (projectiledeflection != ProjectileDeflection.NONE) {
                        break;
                    }
                }

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            vec3 = this.getDeltaMovement();
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;

            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot((float) (Mth.atan2(-d5, -d1) * 180.0F / (float) Math.PI));
            } else {
                this.setYRot((float) (Mth.atan2(d5, d1) * 180.0F / (float) Math.PI));
            }

            this.setXRot((float) (Mth.atan2(d6, d4) * 180.0F / (float) Math.PI));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            if (this.isInWater()) {
                for (int j = 0; j < 4; j++) {
                    float f1 = 0.25F;
                    this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale(f));
            if (!flag) {
                this.applyGravity();
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(
                vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F)
        );
        this.life = 0;
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        if (type != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void tickDespawn() {
        this.life++;
        if (this.life >= maxLife()) {
            this.discard();
        }
    }

    public int maxLife() {
        return 1200;
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.ENTITY) {
            if (this.getPierceLevel() > 0) {
                if (this.piercingIgnoreEntityIds == null) {
                    this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
                }

                if (this.piercedAndKilledEntities == null) {
                    this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
                }

                if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                    this.discard();
                    return;
                }

                this.piercingIgnoreEntityIds.add(((EntityHitResult) result).getEntity().getId());
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

    }

    protected void doKnockback(LivingEntity entity, DamageSource damageSource) {
        double d0 = (this.level() instanceof ServerLevel serverlevel
                ? EnchantmentHelper.modifyKnockback(serverlevel, this.firedFromWeapon, entity, damageSource, 0.0F)
                : 0.0F
        );
        if (d0 > 0.0) {
            double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d0 * 0.6 * d1);
            if (vec3.lengthSqr() > 0.0) {
                entity.push(vec3.x, 0.1, vec3.z);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.lastState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);


        Vec3 vec31 = vec3.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.inGround = true;
        this.shakeTime = 7;
        this.setPierceLevel((byte) 0);
        this.resetPiercedEntities();
    }


    @Nullable
    @Override
    public ItemStack getWeaponItem() {
        return this.firedFromWeapon;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(
                this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
        );
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(target.getId()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short) this.life);
        if (this.lastState != null) {
            compound.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }

        compound.putByte("shake", (byte) this.shakeTime);
        compound.putBoolean("inGround", this.inGround);
        compound.putFloat("damage", this.baseDamage);
        compound.putByte("PierceLevel", this.getPierceLevel());
        BuiltInRegistries.SOUND_EVENT.getResourceKey(this.soundEvent).ifPresent(holder -> {
            compound.putString("SoundEvent", holder.location().toString());
        });
        compound.put("weapon", this.firedFromWeapon.saveOptional(this.registryAccess()));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.life = compound.getShort("life");
        if (compound.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("inBlockState"));
        }

        this.shakeTime = compound.getByte("shake") & 255;
        this.inGround = compound.getBoolean("inGround");
        if (compound.contains("damage", 99)) {
            this.baseDamage = compound.getFloat("damage");
        }

        this.setPierceLevel(compound.getByte("PierceLevel"));
        if (compound.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT
                    .getOptional(ResourceLocation.parse(compound.getString("SoundEvent")))
                    .orElse(this.getDefaultHitGroundSoundEvent());
        }

        this.firedFromWeapon = ItemStack.parseOptional(this.registryAccess(), compound.getCompound("weapon"));
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
    }


    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }


    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public float getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public boolean isAttackable() {
        return this.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
    }


    public void setPierceLevel(byte pierceLevel) {
        this.entityData.set(PIERCE_LEVEL, pierceLevel);
    }

    private void setFlag(int id, boolean value) {
        byte b0 = this.entityData.get(ID_FLAGS);
        if (value) {
            this.entityData.set(ID_FLAGS, (byte) (b0 | id));
        } else {
            this.entityData.set(ID_FLAGS, (byte) (b0 & ~id));
        }
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }


    protected float getWaterInertia() {
        return 0.6F;
    }


    public void setNoPhysics(boolean noPhysics) {
        this.noPhysics = noPhysics;
        this.setFlag(2, noPhysics);
    }

    public boolean isNoPhysics() {
        return !this.level().isClientSide ? this.noPhysics : (this.entityData.get(ID_FLAGS) & 2) != 0;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.inGround;
    }

}
