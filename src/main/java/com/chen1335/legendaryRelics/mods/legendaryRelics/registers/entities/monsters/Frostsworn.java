package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters;

import com.chen1335.legendaryRelics.mods.ironsSpellBooks.ISSModProxy;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai.LRPathNavigation;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 冰誓骑士：近战追击型 Boss。
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

    /** 近战三选一，随机播放。名字与模型动画一一对应。 */
    private static final String[] ATTACK_ANIMS = {"attack_1", "attack_2", "jab"};

    /** 三个攻击在 bb 里命中帧的秒数换算成的 tick（0.29 / 0.2917 / 0.3056）。 */
    private static final Map<String, Integer> ATTACK_HIT_TICKS = Map.of(
            "attack_1", 8,
            "attack_2", 8,
            "jab", 6);

    /** 本次挥击的目标与剩余命中倒计时；动画只在客户端 tick，伤害必须在服务端按同一时刻结算。 */
    private LivingEntity pendingAttackTarget;
    private int pendingHitTicks;

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
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
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

    /** 用精简版导航：原版节点太密，每换一个节点身体朝向就要重算一次。 */
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new LRPathNavigation(this, level);
    }

    /** 只登记目标并触发动画，伤害由服务端在命中时刻结算（见 tick）。 */
    @Override
    public boolean doHurtTarget(Entity target) {
        if (target instanceof LivingEntity living) {
            this.pendingAttackTarget = living;
        }
        String pick = ATTACK_ANIMS[this.getRandom().nextInt(ATTACK_ANIMS.length)];
        this.pendingHitTicks =  Map.of(
                "attack_1", 8,
                "attack_2", 8,
                "jab", 6).getOrDefault(pick, 6);
        this.triggerAnim("attack", pick);
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide() || this.pendingAttackTarget == null) {
            return;
        }

        if (--this.pendingHitTicks > 0) {
            return;
        }

        LivingEntity target = this.pendingAttackTarget;
        this.pendingAttackTarget = null;
        // 与关键帧同刻重新判定：被拉开或已死就是空挥
        if (target.isAlive() && !this.isRemoved() && this.isWithinMeleeAttackRange(target)) {
            super.doHurtTarget(target);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 停下时切 idle 而不是返回 STOP：STOP 会让控制器停下并把骨骼重置回静止姿态，
        // 从走到站会硬切一下。保持控制器运行，transitionLength 才有东西可混合。
        controllers.add(new AnimationController<>(this, "movement", 4,
                state -> state.setAndContinue(state.isMoving() ? WALK : IDLE)));
        // 触发式攻击必须让控制器能回到 STOPPED：GeckoLib 只在 STOPPED 时重置动画 tick
        controllers.add(new AnimationController<>(this, "attack", 3, state -> PlayState.STOP)
                .triggerableAnim("attack_1", ATTACK_1)
                .triggerableAnim("attack_2", ATTACK_2)
                .triggerableAnim("jab", JAB));
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

    /** 脚步声在 Entity 上，不在 Mob 上。 */
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.STRAY_STEP, 0.15F, 1.0F);
    }
}
