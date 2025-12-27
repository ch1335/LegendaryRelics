package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonHelmet extends BlackDragonArmor {

    public BlackDragonHelmet() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.HELMET, new Properties().rarity(Rarity.EPIC));
    }
    @Calculator
    public static FinalCalculator MAGIC_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );
    @Calculator
    public static FinalCalculator GOOD_EFFECT_TIME_MULTIPLIER = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(2F),
                    Constant.of(2.5F)
            )
    );
    @Calculator
    public static FinalCalculator BAD_EFFECT_TIME_MULTIPLIER = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.75F),
                    Constant.of(0.5F)
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.1", MAGIC_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.2", GOOD_EFFECT_TIME_MULTIPLIER.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.3", BAD_EFFECT_TIME_MULTIPLIER.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {
        if (event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
            DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event, -MAGIC_DAMAGE_REDUCE.getValue(arg));
        }
    }
}
