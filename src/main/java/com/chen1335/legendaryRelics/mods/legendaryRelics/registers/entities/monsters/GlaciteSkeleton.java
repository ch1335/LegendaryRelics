package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.goal.forstsworn.FrostShardThrowGoal;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * 冰川骷髅：主手固定霜锥，5 格外投掷，5 格内近战追击。
 *
 * <p>继承 {@code AbstractSkeleton} 而非 {@code Skeleton}：后者的弓相关逻辑用不上，
 * 而前者自带的近战目标正好作为近距离行为。
 */
public class GlaciteSkeleton extends AbstractSkeleton {

    /** 超过这个距离改用投掷。 */
    public static final double RANGED_DISTANCE = 5.0D;

    /** 近战连续够不到多久后转为投掷（tick）。 */
    private static final int MELEE_BLOCKED_TICKS = 40;

    /** 连续近战够不到的时长，被地形挡住时靠它转入投掷。 */
    private int meleeBlockedTicks;

    public GlaciteSkeleton(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
    }

    /** 近战被挡：投掷目标据此接管（见 {@link FrostShardThrowGoal}）。 */
    public boolean isMeleeBlocked() {
        return this.meleeBlockedTicks >= MELEE_BLOCKED_TICKS;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // 判据与 MeleeAttackGoal#canPerformAttack 一致：够不到 + 有视线
        LivingEntity target = this.getTarget();
        if (target != null
                && this.distanceTo(target) <= RANGED_DISTANCE
                && this.getSensing().hasLineOfSight(target)
                && !this.isWithinMeleeAttackRange(target)) {
            this.meleeBlockedTicks++;
        } else {
            this.meleeBlockedTicks = 0;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 优先级 3 高于近战目标（4），所以远距离由投掷接管移动与朝向
        this.goalSelector.addGoal(3, new FrostShardThrowGoal(this));
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // 放在 super 之后，避免被原版装备逻辑覆盖
        this.setItemSlot(EquipmentSlot.MAINHAND, weakenedShard());
        // 手持的这把永不掉落：霜锥只从战利品表出，玩家拿到的是全新的
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return data;
    }

    /** 这把霜锥只有 5 攻击力：2（生物基础）+ 8（武器）+ (-5)（这里补的）。 */
    private static ItemStack weakenedShard() {
        ItemStack stack = new ItemStack(LRItems.FROST_SHARD.get());
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers.withModifierAdded(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(LegendaryRelics.id("glacite_weak_shard"), -5.0D, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND));
        return stack;
    }
}
