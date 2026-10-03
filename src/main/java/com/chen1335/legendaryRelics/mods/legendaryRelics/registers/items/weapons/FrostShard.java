package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.normal.MultiMul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.ThrownFrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRSwordItem;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 霜锥：可以像三叉戟一样蓄力投掷的近战武器。
 *
 * <p>与三叉戟的区别在于投掷代价：投掷**不消耗物品**，只扣 1 点耐久并进入 10 秒冷却，
 * 因此投出的实体也没有拾取机制（见 {@link ThrownFrostShard}）。
 */
public class FrostShard extends LRSwordItem implements IChargeAbleItem {

    /**
     * 蓄力到可以投掷所需的最短时间（tick），与三叉戟一致。
     */
    public static final int THROW_THRESHOLD_TIME = 10;

    /**
     * 投掷初速，与三叉戟一致。
     */
    public static final float SHOOT_POWER = 2.5F;


    @Calculator
    public static final FinalCalculator THROW_DAMAGE = FinalCalculator.of(
            MultiMul.of(
                    Constant.of(0.75F),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    /**
     * 命中实体后溅射出的冰锥的基础伤害：1 + 0.25 × 攻击伤害。
     *
     * <p>溅射算法在本类，冰锥实体只负责承载这个数值（见 {@link ThrownFrostShard}）。
     */
    @Calculator
    public static final FinalCalculator ICICLE_DAMAGE = FinalCalculator.of(
            Add.of(
                    Constant.of(1F),
                    Mul.of(
                            Constant.of(0.25F),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    )
            )
    );

    /**
     * 投掷后的冷却时间（tick），10 秒。
     */
    public static final int THROW_COOLDOWN = 20*3;

    /** 近战直接命中的减速比例。 */
    @Calculator
    public static final FinalCalculator MELEE_SLOWNESS = FinalCalculator.of(Constant.of(0.2F));

    /** 投掷物命中目标的减速比例。 */
    @Calculator
    public static final FinalCalculator THROWN_SLOWNESS = FinalCalculator.of(Constant.of(0.15F));

    /** 冰锥命中目标的减速比例。 */
    @Calculator
    public static final FinalCalculator ICICLE_SLOWNESS = FinalCalculator.of(Constant.of(0.1F));

    /** 减速持续时间（秒）。 */
    @Calculator
    public static final FinalCalculator SLOWNESS_TIME = FinalCalculator.of(Constant.of(3F));

    public FrostShard() {
        super(Tier.Weapon.FROST_SHARD, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant());
    }

    /**
     * 近战直接命中：给目标挂 20% 减速，持续 3 秒。
     *
     * <p>{@code hurtEnemy} 只在服务端、且命中真正生效时被调用（见 {@code Player#attack}），
     * 所以这里不需要额外做客户端判断。
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack);
        Slowness.apply(target, attacker, MELEE_SLOWNESS.getValue(arg), (int) (SLOWNESS_TIME.getValue(arg) * 20));
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg arg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            boolean shift = tooltipFlag.hasShiftDown();
            Component slownessTime = SLOWNESS_TIME.toComponent(shift, arg);
            Component thrownSlowness = THROWN_SLOWNESS.toComponent(shift, arg, Style.EMPTY, 100, "%s%%");
            Component icicleSlowness = ICICLE_SLOWNESS.toComponent(shift, arg, Style.EMPTY, 100, "%s%%");

            // 两行都按像素宽度折行，避免在长行上被硬截断
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.frost_shard.desc.1",
                    slownessTime,
                    MELEE_SLOWNESS.toComponent(shift, arg, Style.EMPTY, 100, "%s%%")
            ).withColor(0xaeaeae), maxToolTipWidth(stack));

            // 物理伤害由 ComponentHolders 统一拼上"物理伤害"字样，所以语言文件里只留 %s
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.frost_shard.desc.2",
                    ComponentHolders.Combat.physicalDamage(THROW_DAMAGE.toComponent(shift, arg)),
                    slownessTime,
                    thrownSlowness,
                    ComponentHolders.Combat.physicalDamage(ICICLE_DAMAGE.toComponent(shift, arg)),
                    slownessTime,
                    icicleSlowness
            ).withColor(0xaeaeae), maxToolTipWidth(stack));

            // 以下两行是背景说明，颜色比正文更暗，与收割者的写法一致
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.frost_shard.desc.3").withColor(5592405), maxToolTipWidth(stack));
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.frost_shard.desc.4").withColor(5592405), maxToolTipWidth(stack));
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        // 只剩最后 1 点耐久时不允许投掷，避免投出去直接把物品用坏
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        int chargeTime = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (chargeTime < THROW_THRESHOLD_TIME) {
            return;
        }

        if (!level.isClientSide) {
            ThrownFrostShard shard = new ThrownFrostShard(player, level, stack);
            shard.setPos(player.getEyePosition());
            shard.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
            shard.setBaseDamage(THROW_DAMAGE.getValue(CalculatorArg.simpleArg(player, stack)));
            level.addFreshEntity(shard);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.50F);

            // 投掷不消耗物品，只扣 1 点耐久
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
        }

        // 投掷后进入 10 秒冷却
        if (!(livingEntity instanceof Player player1 && player1.hasInfiniteMaterials())) {
            player.getCooldowns().addCooldown(this, THROW_COOLDOWN);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public boolean isNoRightClickCooldown() {
        return true;
    }


    @Override
    public int getMaxChargeTick(ItemStack itemStack, LivingEntity user) {
        return THROW_THRESHOLD_TIME;
    }
}
