package com.chen1335.legendaryRelics.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.equipmentEffects.LRBaseEffect;
import com.chen1335.legendaryRelics.utils.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Perforation extends LRBaseEffect {
    public static FinalCalculator DAMAGE = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(3),
                            Constant.of(4)
                    ),
                    Util.ARROW_DAMAGE_MUL
            )
    );

    public Perforation(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public Perforation(int level) {
        super(LREquipmentEffectTypes.PERFORATION.value(), level);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {

    }


}
