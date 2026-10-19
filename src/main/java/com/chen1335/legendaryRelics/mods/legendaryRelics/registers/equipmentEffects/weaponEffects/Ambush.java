package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.registry.ArcanumAttachments;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.utils.LRUtil;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
    @Calculator
    public static final FinalCalculator DAMAGE_MUL = FinalCalculator.of(
            Add.of(
                    EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(3F, 0.25F)),
                    Mul.of(
                            Constant.of(0.5F),
                            EntityAttributeValue.of(ALObjects.Attributes.CRIT_DAMAGE)
                    )
            )
    );


    public Ambush(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        LRUtil.splitAndAdd(
                tooltipComponents,
                Component.translatable("equipment_effect.legendary_relics.ambush",
                        FinalCalculator.format((float) getMaxChargeTick(itemStack, player) / 20, 1),
                        ComponentHolders.Attributes.attribute(
                                Attributes.ATTACK_DAMAGE,
                                DAMAGE_MUL.toComponent(tooltipFlag.hasShiftDown(), CalculatorArg.simpleArg(player, itemStack, this), Style.EMPTY, 100, "%s%%"),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
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
            if (pick instanceof EntityHitResult entityHitResult && chargePercent >= 0.25) {
                AttributeAdjustment attributeAdjustment = new AttributeAdjustment();
                attributeAdjustment.add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL,  DAMAGE_MUL.getValue(arg)-1);
                List<AttributeAdjustment> modifier = player.getData(ArcanumAttachments.ENTITY_DATA.get()).adjustmentData.getModifier(Attributes.ATTACK_DAMAGE);
                modifier.add(attributeAdjustment);
                entityHitResult.getEntity().invulnerableTime = 0;
                player.attack(entityHitResult.getEntity());
                modifier.remove(attributeAdjustment);
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
