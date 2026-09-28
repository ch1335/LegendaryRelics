package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.armorSetEffect;

import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.effectInstances.SoulEffectInstance;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.entities.projectiles.SoulMissile;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects.SoulGather;
import com.chen1335.legendaryRelics.utils.LRColors;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Comparator;
import java.util.List;

public class SoulArmorSetEffect extends SetEffect {
    /**
     * 叠满这个层数后解锁飞弹。
     */
    public static final int MAX_STACK = 10;
    /**
     * 每次触发发射的飞弹数量。
     */
    public static final int MISSILE_COUNT = 3;
    /**
     * 除主目标外，额外选取的周围目标数量上限。
     */
    public static final int NEARBY_TARGET_COUNT = 2;
    /**
     * 搜索周围目标的半径（格）。
     */
    public static final double SEARCH_RADIUS = 5.0;
    /**
     * 飞弹出膛方向相对玩家面朝方向的偏转角（度）：一枚向上，两枚向左、右。
     */
    public static final double LAUNCH_ANGLE = 15.0;
    /**
     * 飞弹出膛位置相对玩家眼睛的偏移距离（格）：一枚向上、一枚向左、一枚向右。
     */
    public static final double LAUNCH_OFFSET = 1.0;

    @Calculator
    public static final FinalCalculator GAIN_STACK_COOLDOWN = FinalCalculator.of(
            TieredBonus.of(List.of(1.5F, 1.5F, 0.75F, 0.5F))
    );

    @Calculator
    public static final FinalCalculator SPELL_POWER_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.03F, 0.03F, 0.04F, 0.05F))
    );

    @Calculator
    public static final FinalCalculator COOLDOWN_REDUCTION_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.01F, 0.012F, 0.015F, 0.02F))
    );

    @Calculator
    public static final FinalCalculator KEEP_TIME = FinalCalculator.of(
            TieredBonus.of(List.of(4F, 4F, 7F, 10F))
    );

    /**
     * 飞弹伤害占「触发这次飞弹的法术伤害」的比例。
     */
    @Calculator
    public static final FinalCalculator MISSILE_DAMAGE_PERCENT = FinalCalculator.of(
            Mul.of(
                    Constant.of(0.15F),
                    TieredBonus.of(List.of(0.64F, 0.64F, 0.81F, 1F))
            )
    );

    /**
     * 目标不足时，多余飞弹打主目标时额外的伤害比例（在飞弹伤害基础上再乘）。
     */
    @Calculator
    public static final FinalCalculator OVERFLOW_DAMAGE_PERCENT = FinalCalculator.of(
            Constant.of(0.5F)
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {
        CalculatorArg args = buildArg(player);
        boolean shiftDown = tooltipFlag.hasShiftDown();
        list.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus",
                Component.translatable("set_effect.legendary_relics.soul.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));
        list.add(Component.translatable("set_effect.legendary_relics.soul.desc.1",
                GAIN_STACK_COOLDOWN.toComponent(shiftDown, args, LRColors.Component.YELLOW.getStyle()),
                ComponentHolders.Combat.magicDamage(),
                soulGatherComponent()
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.soul.desc.2",
                KEEP_TIME.toComponent(shiftDown, args),
                soulGatherComponent(),
                soulGatherComponent()
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.soul.desc.3",
                SPELL_POWER_PER_STACK.toComponent(shiftDown, args, LRColors.Component.BLUE.getStyle(), 100, "%s%%"),
                COOLDOWN_REDUCTION_PER_STACK.toComponent(shiftDown, args, LRColors.Component.BLUE.getStyle(), 100, "%s%%")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.soul.desc.4",
                ComponentHolders.Combat.magicDamage(),
                MISSILE_DAMAGE_PERCENT.toComponent(shiftDown, args, LRColors.Component.RED.getStyle(), 100, "%s%%")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.soul.desc.5",
                OVERFLOW_DAMAGE_PERCENT.toComponent(shiftDown, args, LRColors.Component.BLUE.getStyle(), 100, "%s%%")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.armor_pieces_required", "2+"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Component soulGatherComponent() {
        return ComponentHolders.Effects.soulGather();
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new SoulEffectInstance(piece);
    }

    public static SoulEffectInstance getEffectInstance(LivingEntity living) {
        return EntitySetsEffectData.getSetInstance(living, ISSRegisters.ArmorSetEffect.SOUL_ARMOR.value(), SoulEffectInstance.class);
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!source.is(Tags.DamageTypes.IS_MAGIC) || !(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        SoulEffectInstance instance = getEffectInstance(attacker);
        if (instance == null || instance.getPiece() < 2) {
            return;
        }

        CalculatorArg args = instance.buildArgs(attacker);
        if (instance.coolDown <= 0) {
            SoulGather effect = new SoulGather(SPELL_POWER_PER_STACK.getValue(args), COOLDOWN_REDUCTION_PER_STACK.getValue(args));
            effect.setDecayTime(20);
            effect.initTime((int) (KEEP_TIME.getValue(args) * 20));
            effect.setSourceEntity(attacker);
            instance.coolDown = (int) (GAIN_STACK_COOLDOWN.getValue(args) * 20);
            SpecialEffectAPI.addEffectToEntity(attacker, effect, SoulGather::getFinal);
        }

        SpecialEffectAPI.getEffect(attacker, attacker.getUUID(), ISSRegisters.SpecialMobEffect.SOUL_GATHER.value()).ifPresent(soulGather -> {
            if (soulGather.getStack() >= MAX_STACK) {
                fireVolley(attacker, event.getEntity(), event.getAmount(), args);
            }
        });
    }

    private static void fireVolley(LivingEntity attacker, LivingEntity mainTarget, float triggerDamage, CalculatorArg args) {
        Level level = attacker.level();
        if (level.isClientSide) {
            return;
        }

        float missileDamage = triggerDamage * MISSILE_DAMAGE_PERCENT.getValue(args);
        float overflowDamage = missileDamage * OVERFLOW_DAMAGE_PERCENT.getValue(args);

        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class,
                mainTarget.getBoundingBox().inflate(SEARCH_RADIUS),
                entity -> entity != mainTarget
                        && entity != attacker
                        && entity.isAlive()
                        && !entity.isAlliedTo(attacker)
                        && !entity.isAlliedTo(mainTarget));
        nearby.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(mainTarget)));

        Launch[] launches = buildLaunches(attacker);

        // 上：主目标
        // 左/右：从"主目标周围最近的 N 个"里各分一个，按离各自发射点的距离就近配对，
        //       避免左弹往右飞、右弹往左飞交叉。
        LivingEntity[] assigned = new LivingEntity[MISSILE_COUNT];
        assigned[0] = mainTarget;
        int sideTargetCount = Math.min(NEARBY_TARGET_COUNT, nearby.size());
        if (sideTargetCount == 1) {
            assigned[1] = nearby.get(0);
        } else if (sideTargetCount >= 2) {
            LivingEntity first = nearby.get(0);
            LivingEntity second = nearby.get(1);
            Vec3 eye = attacker.getEyePosition();
            Vec3 leftSpawn = eye.add(launches[1].offset());
            Vec3 rightSpawn = eye.add(launches[2].offset());

            double straight = leftSpawn.distanceToSqr(first.getBoundingBox().getCenter())
                    + rightSpawn.distanceToSqr(second.getBoundingBox().getCenter());
            double crossed = leftSpawn.distanceToSqr(second.getBoundingBox().getCenter())
                    + rightSpawn.distanceToSqr(first.getBoundingBox().getCenter());

            assigned[1] = straight <= crossed ? first : second;
            assigned[2] = straight <= crossed ? second : first;
        }

        for (int i = 0; i < MISSILE_COUNT; i++) {
            // 目标不够分配时，多余的飞弹转而打主目标，只结算一部分伤害
            boolean overflow = assigned[i] == null;
            LivingEntity target = overflow ? mainTarget : assigned[i];
            float damage = overflow ? overflowDamage : missileDamage;
            level.addFreshEntity(new SoulMissile(level, attacker, target, damage, launches[i].direction(), launches[i].offset()));
        }
    }

    /**
     * 三枚飞弹的出膛方向与出膛位置偏移。
     * <p>
     * 方向：以玩家面朝方向为基准，分别向上、向左、向右偏转 {@link #LAUNCH_ANGLE} 度。
     * 位置：相对玩家眼睛分别向上、向左、向右偏移 {@link #LAUNCH_OFFSET} 格。
     * 顺序必须与 {@link #MISSILE_COUNT} 一致。
     */
    private static Launch[] buildLaunches(LivingEntity attacker) {
        Vec3 look = attacker.getLookAngle();
        Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);
        Vec3 right = look.cross(worldUp);
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1.0, 0.0, 0.0) : right.normalize();

        // 左右偏移只取水平方向，避免玩家抬头/低头时偏移跟着上下飘
        Vec3 flatLook = new Vec3(look.x, 0.0, look.z);
        flatLook = flatLook.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : flatLook.normalize();

        return new Launch[]{
                new Launch(rotateAroundAxis(look, right, LAUNCH_ANGLE), worldUp.scale(LAUNCH_OFFSET)),
                new Launch(rotateAroundAxis(look, worldUp, LAUNCH_ANGLE), rotateAroundAxis(flatLook, worldUp, 90.0).scale(LAUNCH_OFFSET)),
                new Launch(rotateAroundAxis(look, worldUp, -LAUNCH_ANGLE), rotateAroundAxis(flatLook, worldUp, -90.0).scale(LAUNCH_OFFSET))
        };
    }

    private record Launch(Vec3 direction, Vec3 offset) {
    }

    /**
     * 罗德里格旋转公式：把向量 {@code v} 绕单位轴 {@code axis} 旋转 {@code degrees} 度。
     */
    private static Vec3 rotateAroundAxis(Vec3 v, Vec3 axis, double degrees) {
        Vec3 unitAxis = axis.normalize();
        double angle = Math.toRadians(degrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return v.scale(cos)
                .add(unitAxis.cross(v).scale(sin))
                .add(unitAxis.scale(unitAxis.dot(v) * (1.0 - cos)));
    }
}
