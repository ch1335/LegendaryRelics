package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.goal.forstsworn;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.GlaciteSkeleton;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.ThrownFrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons.FrostShard;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** 对 5 格外的目标投掷主手霜锥；拉近到射程内后本目标停止，交回近战。 */
public class FrostShardThrowGoal extends Goal {

    /** 抬手蓄力时长（tick），蓄满后再投出。 */
    private static final int CHARGE_TICKS = 20;

    private final GlaciteSkeleton mob;
    private int cooldown;
    private int chargeTicks;

    public FrostShardThrowGoal(GlaciteSkeleton mob) {
        this.mob = mob;
        // 占用移动与朝向，避免和近战目标同时抢方向
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive() || !(this.mob.getMainHandItem().getItem() instanceof FrostShard)) {
            return false;
        }
        if (!this.mob.getSensing().hasLineOfSight(target)) {
            return false;
        }
        // 距离够远，或近战被地形挡住够不到 —— 都转为投掷
        return this.mob.distanceTo(target) > GlaciteSkeleton.RANGED_DISTANCE || this.mob.isMeleeBlocked();
    }

    /** 是否处于近战回退模式：已在近战距离内，但够不到目标。 */
    private boolean meleeFallback() {
        LivingEntity target = this.mob.getTarget();
        return target != null && this.mob.distanceTo(target) <= GlaciteSkeleton.RANGED_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.chargeTicks = 0;
    }

    @Override
    public void stop() {
        this.mob.stopUsingItem();
        this.chargeTicks = 0;
        this.cooldown = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // 回退模式要边推进边投掷；远距离则是原地投掷
        if (this.meleeFallback()) {
            this.mob.getNavigation().moveTo(target, 1.2D);
        } else {
            this.mob.getNavigation().stop();
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }

        // 先抬手蓄力（客户端据此播放举锥姿态），蓄满再投出
        if (!this.mob.isUsingItem()) {
            this.mob.startUsingItem(InteractionHand.MAIN_HAND);
            this.chargeTicks = 0;
            return;
        }
        if (++this.chargeTicks < CHARGE_TICKS) {
            return;
        }
        this.throwShard(target);
        this.mob.stopUsingItem();
        this.cooldown = throwInterval();
    }

    /** 与原版骷髅射箭同节奏：困难 1 秒，其余 2 秒。 */
    private int throwInterval() {
        return this.mob.level().getDifficulty() == Difficulty.HARD ? 20 : 40;
    }

    private void throwShard(LivingEntity target) {
        ItemStack weapon = this.mob.getMainHandItem();
        if (!(weapon.getItem() instanceof FrostShard) || this.mob.level().isClientSide) {
            return;
        }

        ThrownFrostShard shard = new ThrownFrostShard(this.mob, this.mob.level(), weapon);
        shard.setPos(this.mob.getEyePosition());
        double dx = target.getX() - shard.getX();
        // 瞄准头部（眼睛高度）；原版骷髅用的是身高 1/3（腰腹）
        double dy = target.getEyeY() - shard.getY();
        double dz = target.getZ() - shard.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double flightTicks = horizontal / FrostShard.SHOOT_POWER;

        // 按飞行时间预判目标位移
        Vec3 targetMotion = target.getDeltaMovement();
        dx += targetMotion.x * flightTicks;
        dy += targetMotion.y * flightTicks;
        dz += targetMotion.z * flightTicks;
        // 下坠量按投掷物自己的重力算；原版箭矢的 0.2*距离对霜锥补偿过头，会从头顶飞过
        dy += 0.5D * shard.getGravity() * flightTicks * flightTicks;

        // 散布比原版骷髅小一档（原版 14/10/6/2）
        float inaccuracy = Math.max(1.0F, 6.0F - this.mob.level().getDifficulty().getId() * 2.0F);
        shard.shoot(dx, dy, dz, FrostShard.SHOOT_POWER, inaccuracy);
        shard.setBaseDamage(FrostShard.THROW_DAMAGE.getValue(CalculatorArg.simpleArg(this.mob, weapon)));
        this.mob.level().addFreshEntity(shard);

        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.5F);
    }
}
