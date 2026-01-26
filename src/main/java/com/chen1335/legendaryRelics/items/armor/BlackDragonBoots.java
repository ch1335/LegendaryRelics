package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.FallImmunity;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonBoots extends BlackDragonArmor {
    public BlackDragonBoots() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.BOOTS, new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new FallImmunity(1));
    }

    @Calculator
    public static FinalCalculator ENVIRONMENT_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.2F),
                    Constant.of(0.4F)
            )
    );

    @Calculator
    public static FinalCalculator HEALTH_THRESHOLD = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.25F),
                    Constant.of(0.35F)
            )
    );

    @Calculator
    public static FinalCalculator DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.10F),
                    Constant.of(0.15F)
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.1", ENVIRONMENT_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.2", HEALTH_THRESHOLD.toPercentageComponent(tooltipFlag.hasShiftDown(), arg), DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));

    }

    @Override
    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {
        LivingEntity entity = event.getEntity();
        if (event.getSource().is(Tags.DamageTypes.IS_ENVIRONMENT) || event.getSource().is(DamageTypeTags.IS_FALL)) {
            DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event, -ENVIRONMENT_DAMAGE_REDUCE.getValue(arg));
        }
        if (entity.getHealth() < entity.getMaxHealth() * HEALTH_THRESHOLD.getValue(arg)) {
            DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event, -DAMAGE_REDUCE.getValue(arg));
        }
    }
}
