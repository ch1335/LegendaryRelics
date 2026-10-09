package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters;

import com.chen1335.legendaryRelics.mods.ironsSpellBooks.ISSModProxy;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.LRPathNavigation;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.goal.AnimatableMeleeAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.builtin.AutoPlayingSoundKeyframeHandler;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 冰誓骑士
 *
 * <p>{@code Monster#registerGoals} 是空实现，走路与攻击目标都得自己加。
 * 模型里 attack_1 会带动 hand_item 骨骼，主手武器由渲染层的 BlockAndItemGeoLayer 挂上。
 */
public class Frostsworn extends Monster implements GeoEntity {

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    // thenPlay 转发给 json 的 loop 字段：hold_on_last_frame 会把控制器置为 PAUSED 而非 STOPPED，
    // 姿势得以保留，不会在 getBoneResetTime（5 tick）后被重置回静止。
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("attack_1");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("attack_2");
    private static final RawAnimation JAB = RawAnimation.begin().thenPlay("jab");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Frostsworn(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 8.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // 参数依次是 权重 / 命中帧 / 动画长度（tick）。命中帧要和 bb 里标的关键帧对齐，
        // 动画长度决定两击之间的最短间隔。
        int attackTransitionTickTime = 1;
        this.goalSelector.addGoal(2, new AnimatableMeleeAttackGoal<>(this, 1.0D, true)
                .withAttackType(1, 8 + attackTransitionTickTime, 15, "attack", "attack_1", (mob, target) -> mob.doHurtTarget(target))
                .withAttackType(1, 8 + attackTransitionTickTime, 15, "attack", "attack_2", (mob, target) -> mob.doHurtTarget(target))
                .withAttackType(1, 6 + attackTransitionTickTime, 13, "attack", "jab", (mob, target) -> mob.doHurtTarget(target)));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * 默认近战判定是「双方命中箱相交」，宽 0.7 的 Boss 对宽 0.6 的玩家要贴到约 0.65 格才算得到，
     * 而实体不能重叠，于是常停在临界距离上站着不动手。外扩 1 格（reach 约 1.65 格）即可。
     */
    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(1D, 0.0D, 1D);
    }

    /**
     * 用精简版导航：原版节点太密，每换一个节点身体朝向就要重算一次。
     */
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new LRPathNavigation(this, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<Frostsworn> attackController = new AnimationController<>(this, "attack", 1, state -> {
            state.resetCurrentAnimation();
            return PlayState.STOP;
        })
                // 关键帧音效默认不会播，挂上内置的自动播放 handler 才会响应 sound_effects
                .setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>())
                .triggerableAnim("attack_1", ATTACK_1)
                .triggerableAnim("attack_2", ATTACK_2)
                .triggerableAnim("jab", JAB);

        AnimationController<Frostsworn> moveController = new AnimationController<>(this, "movement", 3,
                state -> {

                    if (attackController.isPlayingTriggeredAnimation()) {
                        state.resetCurrentAnimation();
                        return PlayState.STOP;
                    }
                    return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
                });

        controllers.add(moveController);
        controllers.add(attackController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // 放在 super 之后，避免被原版装备逻辑覆盖；该物品属于联动模块，未加载时注册项不存在
        if (ISSModProxy.INSTANCE.shouldLoad()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ISSRegisters.Item.MAGIC_BREAKER.get()));
            // 手上这把永不掉落
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
        return data;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    /**
     * 脚步声在 Entity 上，不在 Mob 上。
     */
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.STRAY_STEP, 0.15F, 1.0F);
    }
}
