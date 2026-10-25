package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class AgglomerationMaliceEffect extends LRCurioEffect {

    public AgglomerationMaliceEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Calculator
    public static final FinalCalculator DAMAGE_MULTIPLIER = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(3F),
            Constant.of(2F)
    )).withFormat(Format.PERCENTAGE);

    @Calculator
    public static final FinalCalculator LOOTING_LEVEL_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(2),
            Constant.of(3F)
    ));
    @Calculator
    public static final FinalCalculator BONUS_LOOT_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.5F),
            Constant.of(0.75F)
    )).withFormat(Format.PERCENTAGE);

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.desc").withColor(5592405));
        tooltipComponents.add(Component.empty());
        Component damageMultiplier = ComponentUtil.Attributes.attribute(ArcanumAttributes.DAMAGE_MULTIPLIER,
                DAMAGE_MULTIPLIER.toComponent(args),
                "×");
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.bad_effect", damageMultiplier).withColor(16733525));
        Component looting = ComponentUtil.Attributes.attribute(ArcanumAttributes.LOOTING,
                LOOTING_LEVEL_ADD.toComponent(args),
                "+");
        Component bonusLoot = ComponentUtil.Attributes.attribute(ArcanumAttributes.BONUS_LOOT,
                BONUS_LOOT_ADD.toComponent(args),
                "+");
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.good_effect", looting, bonusLoot).withColor(0xaeaeae));
    }

    @Override
    public int getRawEffectLevel() {
        return 1;
    }


    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Mob) {
            if (((Mob) attacker).getSpawnType() == MobSpawnType.SPAWNER) {
                EquipmentEffectAPI.findBestEffect(target, LREquipmentEffectTypes.AGGLOMERATION_MALICE_EFFECT.value()).ifPresent(pair -> {
                    CalculatorArg args = CalculatorArg.simpleArg(target, pair.infoHolder().itemStack(), pair.infoHolder().effect());
                    float value = DAMAGE_MULTIPLIER.getValue(args);
                    event.getAdjustment(attacker, ArcanumAttributes.DAMAGE_MULTIPLIER).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, value - 1);
                });
            }
        }
    }


    public static void getLootingAdjustment(LivingGetAttributeAdjustmentEvent event, LootContext lootContext, LivingEntity attacker) {
        if (lootContext.getParam(LootContextParams.THIS_ENTITY) instanceof Mob mob && mob.getSpawnType() == MobSpawnType.SPAWNER) {
            EquipmentEffectAPI.findBestEffect(attacker, LREquipmentEffectTypes.AGGLOMERATION_MALICE_EFFECT.value()).ifPresent(pair -> {
                CalculatorArg args = CalculatorArg.simpleArg(attacker, pair.infoHolder().itemStack(), pair.infoHolder().effect());
                double lootingLevelAddition = LOOTING_LEVEL_ADD.getValue(args);
                double bonusLootAddition = BONUS_LOOT_ADD.getValue(args);
                event.getAdjustment(attacker, ArcanumAttributes.LOOTING).add(AttributeAdjustment.Operation.ADD_VALUE, lootingLevelAddition);
                event.getAdjustment(attacker, ArcanumAttributes.BONUS_LOOT).add(AttributeAdjustment.Operation.ADD_VALUE, bonusLootAddition);
            });
        }
    }
}