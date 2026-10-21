package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 霜钢剑：右键蓄力，蓄力时长受攻击速度影响；具体效果留给后续设计。
 */
public class FrostSteelSword extends LRMeleeWeaponBase implements IChargeAbleItem {
    public static final ResourceLocation THRUST_CHARGE = LegendaryRelics.id("thrust_charge");
    public static final ResourceLocation THRUST_SPRINT = LegendaryRelics.id("thrust_sprint");
    /** 蓄力低于这个比例松开不冲刺。 */
    private static final float MIN_CHARGE_PERCENT = 0.25F;
    @Calculator
    public static final FinalCalculator MAX_CHARGE_TICKS = FinalCalculator.of(Constant.of(40F));
    /** 冲刺水平速度（格/tick）。 */
    @Calculator
    public static final FinalCalculator DASH_POWER = FinalCalculator.of(Constant.of(2F));
    /** 释放后的物品冷却（秒）。 */
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
        Vec3 dash = horizontal.normalize().scale(DASH_POWER.getValue(arg));
        player.setDeltaMovement(dash.x, 0.1, dash.z);
        player.hurtMarked = true;   // 让客户端收到速度同步
        player.setSprinting(true);
        player.resetFallDistance();

        // 蓄力动画的 checker 会在松开后清空动画层，这里先手动清掉状态，避免它把冲刺动画一起清掉
        LRPlayerData.getData(player).playerAnimationState = null;
        PlayerAnimationHandler.play(player, THRUST_SPRINT, 2);

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
}
