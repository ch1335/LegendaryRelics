package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 动画驱动的近战攻击目标：按权重选一招、触发动画、在命中帧结算。
 *
 * <p>选招后到 animTime 走完之间不重抽，所以动画长度就是两击之间的最短间隔。
 */
public class AnimatableMeleeAttackGoal<T extends PathfinderMob & GeoEntity> extends MeleeAttackGoal {
    private final T animatable;
    private final List<AttackType<T>> attackTypeList = new ArrayList<>();

    private int attackTick;
    private int animTick;
    private AttackType<T> currentAttack;

    public AnimatableMeleeAttackGoal(T mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.animatable = mob;
    }

    public AnimatableMeleeAttackGoal<T> withAttackType(int weight, int attackTime, int animTime, String controllerName, String animName, BiConsumer<T, LivingEntity> action) {
        attackTypeList.add(new AttackType<>(weight, attackTime, animTime, controllerName, animName, action));
        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.currentAttack == null) {
            return;
        }

        if (this.attackTick > 0) {
            this.attackTick--;
            if (this.attackTick == 0) {
                LivingEntity target = this.animatable.getTarget();
                // 与关键帧同刻重新判定：被拉开或已死就是空挥
                if (target != null && target.isAlive()) {
                    this.currentAttack.action().accept(this.animatable, target);
                }
            }
        }

        if (this.animTick > 0) {
            this.animTick--;
            if (this.animTick == 0) {
                this.currentAttack = null;
            }
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            if (this.currentAttack != null) {
                return;
            }

            AttackType<T> picked = this.pickWeightedAttack();
            if (picked == null) {
                return;
            }

            this.currentAttack = picked;
            this.attackTick = picked.attackTime();
            this.animTick = picked.animTime();
            // 服务端调用时会自动发包同步到客户端，见 GeoEntity#triggerAnim
            this.animatable.triggerAnim(picked.controllerName(), picked.animName());
        }
    }

    /**
     * 按权重抽一个攻击，权重是可加性的占比；权重非正的项永远抽不到。
     *
     * <p>用 mob 自己的随机源，保证同一存档下由世界种子决定、且不额外分配 RandomSource。
     */
    private AttackType<T> pickWeightedAttack() {
        int total = 0;
        for (AttackType<T> attack : this.attackTypeList) {
            total += Math.max(attack.weight(), 0);
        }
        if (total <= 0) {
            return null;
        }

        int roll = this.animatable.getRandom().nextInt(total);
        for (AttackType<T> attack : this.attackTypeList) {
            int weight = Math.max(attack.weight(), 0);
            if (roll < weight) {
                return attack;
            }
            roll -= weight;
        }

        return null;
    }

    /** attackTime 是距起手的命中帧 tick，animTime 是整段动画长度；后者必须不小于前者。 */
    public record AttackType<T extends PathfinderMob & GeoEntity>(int weight, int attackTime, int animTime,
                                                                  String controllerName,
                                                                  String animName,
                                                                  BiConsumer<T, LivingEntity> action) {

    }
}
