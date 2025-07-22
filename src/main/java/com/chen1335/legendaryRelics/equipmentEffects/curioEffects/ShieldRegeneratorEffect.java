package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRShieldType;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import com.chen1335.shieldSystem.shieldSystem.ShieldInstanceHolder;
import com.chen1335.shieldSystem.shieldSystem.UnitShield;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShieldRegeneratorEffect extends LRCurioEffectBase {
    public ShieldRegeneratorEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public ShieldRegeneratorEffect(int level) {
        this(LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), level);
    }


    public static FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(3),
            Constant.of(2.5F)
    ));
    public static FinalCalculator AMOUNT_PER_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(1),
            Constant.of(1.2F)
    ));
    public static FinalCalculator MAX_SHIELD = FinalCalculator.of(Mul.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            ),
            EntityAttributeValue.of(Attributes.MAX_HEALTH)
    ));

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.shield_regenerator.skill.1",
                COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args),
                AMOUNT_PER_ADD.toComponent(tooltipFlag.hasShiftDown(), args),
                MAX_SHIELD.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
    }

    @Override
    public void curioTick(ItemStack itemStack, LivingEntity wearer) {
        CalculatorArg args = CalculatorArg.simpleArg(wearer, itemStack, this);
        if (wearer.level().getGameTime() % (COOLDOWN.getInt(args) * 20L) == 0) {
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(wearer, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null) {
                instance.getShield().addShieldAmount(AMOUNT_PER_ADD.getValue(args), MAX_SHIELD.getValue(args));
            }
        }
    }
}
