package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.animation.KeyframeAction;
import com.chen1335.legendaryRelics.animation.PlayerAnimationState;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.PlayerAnimationHandler;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.legendaryRelics.utils.SimpleSchedule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 霜钢剑：右键蓄力，蓄力时长受攻击速度影响；具体效果留给后续设计。
 */
public class FrostSteelSword extends LRMeleeWeaponBase implements IChargeAbleItem {
    public static final ResourceLocation THRUST_CHARGE = LegendaryRelics.id("thrust_charge");
    public static final ResourceLocation THRUST_SPRINT = LegendaryRelics.id("thrust_sprint");
    /**
     * 蓄力低于这个比例松开不冲刺。
     */
    private static final float MIN_CHARGE_PERCENT = 0.25F;
    @Calculator
    public static final FinalCalculator MAX_CHARGE_TICKS = FinalCalculator.of(Constant.of(40F));
    /**
     * 冲刺水平速度（格/tick）。
     */
    @Calculator
    public static final FinalCalculator DASH_POWER = FinalCalculator.of(Constant.of(2F));
    /**
     * 释放后的物品冷却（秒）。
     */
    @Calculator
    public static final FinalCalculator RELEASE_COOLDOWN = FinalCalculator.of(Constant.of(0.4F));

    public FrostSteelSword() {
        super(Tier.Weapon.FROST_STEEL_SWORD, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        PlayerAnimationHandler.play(player, THRUST_CHARGE, 2);
        LRPlayerData.getData(player).playerAnimationState = PlayerAnimationState.start(Integer.MAX_VALUE)
                .checker(() -> player.getMainHandItem().is(this) && player.isUsingItem())
                .addKeyframeAction(26, new KeyframeAction.Run((state) -> {
                    PlayerAnimationHandler.playFrom(player, THRUST_CHARGE, 5, 2);
                    state.setTime(5);
                    state.resetKeyframe();
                }));
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (level.isClientSide || !(livingEntity instanceof Player player)) {
            return;
        }
        if (getChargePercent(stack, player) < MIN_CHARGE_PERCENT) {
            return;
        }
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            return;
        }
        CalculatorArg arg = CalculatorArg.simpleArg(player, stack);
        Vec3 dashDirection = horizontal.normalize();
        Vec3 dash = dashDirection.scale(DASH_POWER.getValue(arg));
        player.setDeltaMovement(dash.x, 0.1, dash.z);
        player.hurtMarked = true;   // 让客户端收到速度同步
        player.setSprinting(true);
        player.resetFallDistance();

        // 蓄力动画的 checker 会在松开后清空动画层，这里先手动清掉状态，避免它把冲刺动画一起清掉
        LRPlayerData data = LRPlayerData.getData(player);
        data.playerAnimationState = null;
        PlayerAnimationHandler.play(player, THRUST_SPRINT, 2);
        ThrustSchedule schedule = new ThrustSchedule(player, dashDirection);
        data.addInvincibleSource(schedule);
        data.addInvinciblePushSource(schedule);
        SimpleSchedule.addSchedule(player.level(), schedule);
        int cooldown = Math.max(1, Math.round(RELEASE_COOLDOWN.getValue(arg) * 20.0F));
        player.getCooldowns().addCooldown(this, cooldown);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_RIPTIDE_1.value(), SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    /**
     * 当前蓄力比例（0~1），后续做释放效果时直接用。
     */
    public float getChargePercent(ItemStack stack, LivingEntity user) {
        int maxChargeTick = getMaxChargeTick(stack, user);
        int chargeTick = Math.min(maxChargeTick, getUseDuration(stack, user) - user.getUseItemRemainingTicks());
        return (float) chargeTick / maxChargeTick;
    }

    @Override
    public int getMaxChargeTick(ItemStack stack, LivingEntity user) {
        float base = MAX_CHARGE_TICKS.getValue(CalculatorArg.simpleArg(user, stack));
        double attackSpeed = user.getAttributeValue(Attributes.ATTACK_SPEED);
        return Math.max(1, (int) (base * 2.0 / Math.max(0.01, attackSpeed)));
    }

    @Override
    public boolean isNoRightClickCooldown() {
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SWORD_DIG;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        Player player = LRClient.getClientPlayer();
        if (level == null || !level.isClientSide || player == null) {
            return;
        }
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.frost_steel_sword.desc.1",
                FinalCalculator.format((float) getMaxChargeTick(stack, player) / 20.0F, 1)
        ).withColor(0xaeaeae), maxToolTipWidth(stack));
        tooltipComponents.add(Component.translatable("legendary_relics.max_charge_time_affected_by_attack_speed")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    /**
     * 冲刺过程：释放瞬间给速度，之后靠玩家自身动量衰减，低于阈值即视为冲刺结束。
     * <p>冲刺期间对前方生物造成一次固定伤害。
     */
    public static class ThrustSchedule extends SimpleSchedule.Schedule {
        /**
         * 冲刺命中伤害（固定值）。
         */
        public static final float HIT_DAMAGE = 5.0F;
        /**
         * 冲刺方向额外够到的距离（格）。
         */
        private static final double REACH = 2.0;
        /**
         * 左右两侧额外加宽的距离（格）。
         */
        private static final double SIDE_REACH = 1.0;
        /**
         * 与 ProjectileUtil 一致的判定膨胀。
         */
        private static final float HIT_INFLATE = 0.3F;
        /**
         * 兜底最大持续 tick，防止动量判定卡住。
         */
        private static final int MAX_TICKS = 60;

        private final Player player;
        private final Vec3 direction;
        /** 已命中的目标，同一次冲刺只打一次。 */
        private final Set<UUID> hitTargets = new HashSet<>();

        public ThrustSchedule(Player player, Vec3 direction) {
            this.player = player;
            this.direction = direction;
        }

        @Override
        public boolean finished() {
            if (this.player.isRemoved() || this.time > MAX_TICKS) {
                return true;
            }
            Vec3 movement = this.player.getDeltaMovement();
            double horizontalSqr = movement.x * movement.x + movement.z * movement.z;
            // 正常移速 = 玩家的移动速度属性（含药水/装备修正）
            double endSpeed = this.player.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1;
            return horizontalSqr < endSpeed * endSpeed;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.player.level().isClientSide) {
                return;
            }
            this.hurtTargetsInFront();
        }

        private void hurtTargetsInFront() {
            AABB playerBox = this.player.getBoundingBox();
            double halfWidth = playerBox.getXsize() * 0.5;
            // 冲刺方向的左右法线，用来把搜索框往两侧各加宽 1 格
            Vec3 side = new Vec3(-this.direction.z, 0.0, this.direction.x);
            // 搜索框：玩家 AABB 两倍 + 朝冲刺方向延伸 2 格 + 左右各 1 格
            AABB searchBox = playerBox.inflate(halfWidth, playerBox.getYsize() * 0.5, halfWidth)
                    .expandTowards(this.direction.x * (REACH + halfWidth), 0.0, this.direction.z * (REACH + halfWidth))
                    .expandTowards(side.x * SIDE_REACH, 0.0, side.z * SIDE_REACH)
                    .expandTowards(-side.x * SIDE_REACH, 0.0, -side.z * SIDE_REACH);
            DamageSource source = this.player.damageSources().source(LRDamageTypes.FROST_STEEL_THRUST, this.player);
            for (LivingEntity target : this.player.level().getEntitiesOfClass(LivingEntity.class, searchBox, this::canHit)) {
                // 用体积相交而不是细线段，左右加宽出来的空间才能真正打到目标
                if (!searchBox.intersects(target.getBoundingBox().inflate(HIT_INFLATE))) {
                    continue;
                }
                if (!this.hitTargets.add(target.getUUID())) {
                    continue;
                }
                if (!target.hurt(source, HIT_DAMAGE)) {
                    continue;
                }
                // 命中沿冲刺方向击退；走原版 knockback，能吃到击退抗性/事件
                target.knockback(1, this.direction.x, this.direction.z);
            }
        }

        /** 参考投射物的目标筛选：排除自己、观众、友方，且需要视线。 */
        private boolean canHit(LivingEntity target) {
            return target != this.player
                    && target.isAlive()
                    && !target.isSpectator()
                    && !this.player.isAlliedTo(target)
                    && this.player.hasLineOfSight(target);
        }

        @Override
        public void onFinished() {
            LRPlayerData.getData(player).removeInvincibleSource(this);
            LRPlayerData.getData(player).removeInvinciblePushSource(this);
        }
    }
}
