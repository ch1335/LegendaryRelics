package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HealPerSecondEffect extends LRCurioEffectBase {
    public HealPerSecondEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public HealPerSecondEffect(int level) {
        this(LREquipmentEffectTypes.HEAL_PER_SECOND_EFFECT.value(), level);
    }

    public static FinalCalculator HEAL_PER_5S = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(level -> 0.5F + level * 0.5F)
    ));

    @Override
    public void tick(ItemStack itemStack, LivingEntity wearer) {
        CalculatorArg calculatorArg = CalculatorArg.simpleArg(wearer, itemStack, this);
        if (wearer.level().getGameTime() % 10 == 0) {
            wearer.heal(HEAL_PER_5S.getValue(calculatorArg) / 10);
        }
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.healing_talisman.desc.2", HEAL_PER_5S.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }
}
