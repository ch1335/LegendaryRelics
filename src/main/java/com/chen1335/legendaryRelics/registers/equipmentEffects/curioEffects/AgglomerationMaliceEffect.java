package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.EventWrapper;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class AgglomerationMaliceEffect extends LRCurioEffect {

    public AgglomerationMaliceEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Calculator
    public static final FinalCalculator DAMAGE_MULTIPLIER = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(2),
            Constant.of(1.5F)
    ));

    @Calculator
    public static final FinalCalculator LOOTING_LEVEL_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(2),
            Constant.of(3F)
    ));

    @Override
    public int modifyLootingLevel(ItemStack itemStack, LivingEntity livingTarget, LivingEntity livingAttacker, int lootingLevel) {
        if (livingTarget instanceof Mob mob && mob.getSpawnType() == MobSpawnType.SPAWNER) {
            CalculatorArg args = CalculatorArg.simpleArg(livingAttacker, itemStack, this);
            int lootingLevelAddition = (int) LOOTING_LEVEL_ADD.getValue(args);
            return lootingLevel + lootingLevelAddition;
        }

        return lootingLevel;
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.desc").withColor(5592405));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.bad_effect", DAMAGE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(16733525));
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.good_effect", LOOTING_LEVEL_ADD.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));

    }

    @Override
    public int getRawEffectLevel() {
        return 1;
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Mob attacker) {
            LivingEntity livingEntity = event.getEntity();
            if (attacker.getSpawnType() == MobSpawnType.SPAWNER) {
                EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.AGGLOMERATION_MALICE_EFFECT.value()).ifPresent(pair -> {
                    CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.itemStack(), pair.effect());
                    DamageControllerAPI.addMultipliedBase((IDamageContainerGetter) event, DAMAGE_MULTIPLIER.getValue(args));
                });
            }
        }
    }
}
