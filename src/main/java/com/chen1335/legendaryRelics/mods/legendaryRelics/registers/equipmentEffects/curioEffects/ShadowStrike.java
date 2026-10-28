package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.ShadowStrikeEffect;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 暗影突袭：按下按键传送到准星所指生物背后，下一次近战攻击获得伤害修正。
 */
public class ShadowStrike extends LRCurioEffect {

    public static final int BUFF_TICKS = 100;

    /** 由客户端初始化时替换成真实按键显示，服务端保持默认值。 */
    public static Supplier<Component> keyDisplay = () -> Component.literal("[R]");

    @Calculator
    public static final FinalCalculator COOLDOWN = FinalCalculator.of(
            Constant.of(10.0F)
    ).withFormat(Format.NORMAL);

    @Calculator
    public static final FinalCalculator RANGE = FinalCalculator.of(
            Constant.of(24.0F)
    ).withFormat(Format.NORMAL);

    @Calculator
    public static final FinalCalculator DAMAGE_MULTIPLIER = FinalCalculator.of(
            Constant.of(1.2F)
    ).withFormat(Format.PERCENTAGE);

    public ShadowStrike(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this).withTooltipFlag(tooltipFlag);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.shadow_strike",
                keyDisplay.get(),
                RANGE.toComponent(args),
                ComponentHolders.Combat.attack(),
                ComponentUtil.Attributes.attribute(ArcanumAttributes.DAMAGE_MULTIPLIER, DAMAGE_MULTIPLIER.toComponent(args), "×")
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
        tooltipComponents.add(Component.translatable("legendary_relics.cooldown", COOLDOWN.toComponent(args)).withColor(5592405));
    }

    public static void tryActivate(ServerPlayer player, @Nullable Entity targetEntity) {
        if (!(targetEntity instanceof LivingEntity target) || target == player) {
            return;
        }
        if (!target.isAlive() || target.isSpectator() || player.level() != target.level()) {
            return;
        }
        Optional<SlotEffectHolder<ShadowStrike>> effectHolder = EquipmentEffectAPI.findBestEffect(player, LREquipmentEffectTypes.SHADOW_STRIKE.value());
        float range = RANGE.getValue(CalculatorArg.simpleArg(player));
        if (effectHolder.isEmpty()
                || !EquipmentEffectCooldownManager.isNotInCooldown(player, LREquipmentEffectTypes.SHADOW_STRIKE.value())
                || player.distanceToSqr(target) > (double) range * range
                || !player.hasLineOfSight(target)) {
            return;
        }

        Vec3 look = target.getLookAngle();
        Vec3 behindDir = new Vec3(look.x, 0.0D, look.z);
        if (behindDir.lengthSqr() < 1.0E-4D) {
            behindDir = Vec3.atLowerCornerOf(target.getDirection().getNormal());
        }
        Vec3 behind = target.position().subtract(behindDir.normalize().scale(1.2D));

        double x = behind.x;
        double z = behind.z;
        double y = target.getY();
        for (double offset : new double[]{0.0D, -1.0D, 1.0D, -2.0D, 2.0D}) {
            double candidateY = target.getY() + offset;
            AABB box = player.getBoundingBox().move(x - player.getX(), candidateY - player.getY(), z - player.getZ());
            if (player.level().noCollision(player, box)) {
                y = candidateY;
                break;
            }
        }

        double dx = target.getX() - x;
        double dz = target.getZ() - z;
        float yRot = (float) (Mth.atan2(-dx, dz) * 180.0D / Math.PI);
        double dy = target.getEyeY() - (y + player.getEyeHeight());
        float xRot = (float) (-Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 180.0D / Math.PI);
        player.teleportTo((ServerLevel) player.level(), x, y, z, Set.<RelativeMovement>of(), yRot, xRot);
        player.setDeltaMovement(Vec3.ZERO);

        ServerLevel level = (ServerLevel) player.level();
        level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.sendParticles(ParticleTypes.PORTAL, x, y + 1.0D, z, 24, 0.4D, 0.6D, 0.4D, 0.1D);

        ShadowStrikeEffect effect = new ShadowStrikeEffect(LRSpecialMobEffects.SHADOW_STRIKE.value());
        effect.setSourceEntity(player);
        effect.initTime(BUFF_TICKS);
        SpecialEffectAPI.addEffectToEntity(player, effect, (theNew, theOld) -> theNew);

        int cooldownTicks = Math.max(1, (int) (COOLDOWN.getValue(CalculatorArg.simpleArg(player)) * 20));
        EquipmentEffectCooldownManager.addCooldown(player, LREquipmentEffectTypes.SHADOW_STRIKE.value(), cooldownTicks);
    }

    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, LivingEntity attacker) {
        Optional<ShadowStrikeEffect> effect = SpecialEffectAPI.getEffect(attacker, attacker.getUUID(), LRSpecialMobEffects.SHADOW_STRIKE.value());
        if (effect.isEmpty()) {
            return;
        }
        CalculatorArg args = CalculatorArg.simpleArg(attacker, attacker.getMainHandItem());
        float multiplier = DAMAGE_MULTIPLIER.getValue(args);
        event.getAdjustment(attacker, ArcanumAttributes.DAMAGE_MULTIPLIER)
                .add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, multiplier - 1.0F);
        SpecialEffectAPI.removeEffect(attacker, attacker.getUUID(), LRSpecialMobEffects.SHADOW_STRIKE.value());
    }
}
