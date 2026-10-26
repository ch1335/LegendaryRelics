package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.arcanumlib.common.attachmentTypes.EntityData;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import com.chen1335.legendaryRelics.utils.LRUtil;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class Ambush extends LRWeaponEffect {
    public static final Object AMBUSH = new Object();
    @Calculator
    public static final FinalCalculator DAMAGE_MUL = FinalCalculator.of(
            Add.of(
                    EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(2F, 0.25F)),
                    Mul.of(
                            Constant.of(0.5F),
                            EntityAttributeValue.of(ALObjects.Attributes.CRIT_DAMAGE)
                    )
            )
    ).withFormat(Format.PERCENTAGE);


    public Ambush(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        LRUtil.splitAndAdd(
                tooltipComponents,
                Component.translatable("equipment_effect.legendary_relics.ambush",
                        FinalCalculator.format((float) getMaxChargeTick(itemStack, player) / 20, 1),
                        ComponentUtil.Attributes.attribute(
                                Attributes.ATTACK_DAMAGE,
                                DAMAGE_MUL.toComponent(CalculatorArg.simpleArg(player, itemStack, this).withTooltipFlag(tooltipFlag)),
                                "×"
                        )
                ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack)
        );
        tooltipComponents.add(Component.translatable("legendary_relics.max_charge_time_affected_by_attack_speed").withStyle(ChatFormatting.DARK_GRAY));

    }


    public void handleReleaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        CalculatorArg arg = CalculatorArg.simpleArg(livingEntity, stack, this);
        if (livingEntity instanceof Player player) {
            double entityInteractionRange = livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
            float chargePercent = getChargePercent(stack, livingEntity);
            HitResult pick = LRUtil.pick(livingEntity, 0, entityInteractionRange);
            if (pick instanceof EntityHitResult entityHitResult && chargePercent >= 0.2) {
                LREntityData data = LREntityData.getData(player);
                entityHitResult.getEntity().invulnerableTime = 0;
                data.addAdjustmentConsumer(AMBUSH, event -> {
                    float value = DAMAGE_MUL.getValue(arg);
                    event.getAdjustment(player, Attributes.ATTACK_DAMAGE).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, (value - 1) * chargePercent);
                });
                player.attack(entityHitResult.getEntity());
                data.removeAdjustmentConsumer(AMBUSH);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    public float getChargePercent(ItemStack stack, LivingEntity entity) {
        int maxChargeTick = getMaxChargeTick(stack, entity);
        int chargeTick = Math.min(maxChargeTick, stack.getItem().getUseDuration(stack, entity) - entity.getUseItemRemainingTicks());
        return (float) chargeTick / maxChargeTick;
    }

    public int getMaxChargeTick(ItemStack itemStack, LivingEntity user) {
        if (user == null) {
            return 40;
        }
        int maxTick = 40;
        if (user.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)) {
            double attributeValue = user.getAttributeValue(Attributes.ATTACK_SPEED);
            maxTick = (int) ((double) maxTick * (2D / attributeValue));
        }
        return maxTick;
    }
}