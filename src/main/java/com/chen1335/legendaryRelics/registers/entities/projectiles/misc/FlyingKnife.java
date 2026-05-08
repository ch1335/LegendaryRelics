package com.chen1335.legendaryRelics.registers.entities.projectiles.misc;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityDataSerializers;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.client.renderUtils.trail.TrailHolder;
import com.chen1335.legendaryRelics.client.renderUtils.trail.TrailHolderManager;
import com.chen1335.legendaryRelics.registers.entities.projectiles.LRProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public class FlyingKnife extends LRProjectile {

    private static final EntityDataAccessor<Item> ITEM = SynchedEntityData.defineId(FlyingKnife.class, LREntityDataSerializers.ITEM.get());

    private ItemStack renderItem = null;

    public FlyingKnife(EntityType<? extends LRProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlyingKnife(LivingEntity owner, Level level, @Nullable ItemStack firedFromWeapon) {
        super(LREntityTypes.FLYING_KNIFE.value(), owner, level, firedFromWeapon);
        if (firedFromWeapon != null) {
            entityData.set(ITEM, firedFromWeapon.getItem());
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (level().isClientSide) {
            TrailHolderManager.add(new TrailHolder(this::isRemoved, this::getPosition, 16,10));
        }
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ITEM, Items.AIR);
    }

    @Override
    public int maxLife() {
        return 200;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public ItemStack getRenderItem() {
        if (renderItem == null) {
            renderItem = entityData.get(ITEM).getDefaultInstance();
        }
        return renderItem;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        DamageSource source = this.level().damageSources().source(LRDamageTypes.FLYING_KNIFE, this, this.getOwner());
        float damage = getBaseDamage();
        if (!level().isClientSide && getWeaponItem() != null) {
            damage = EnchantmentHelper.modifyDamage((ServerLevel) level(), this.getWeaponItem(), entity, source, damage);
        }
        if (entity.hurt(source, damage)) {
            if (!level().isClientSide && getWeaponItem() != null) {
                DamageSource directSource = this.level().damageSources().source(LRDamageTypes.FLYING_KNIFE, this.getOwner());
                EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) level(), entity, directSource, getWeaponItem());
            }
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.5F);
        } else {
            this.deflect(ProjectileDeflection.REVERSE, entity, this.getOwner(), false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
        }

        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.playSound(SoundEvents.TRIDENT_HIT_GROUND, 0.7F, 1.2F / (this.random.nextFloat() * 0.1F + 0.3F));
    }
}
