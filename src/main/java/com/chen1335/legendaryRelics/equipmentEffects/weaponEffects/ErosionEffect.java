package com.chen1335.legendaryRelics.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.specialMobEffects.Erosion;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ErosionEffect extends LRWeaponEffect {
    public ErosionEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public ErosionEffect(int level) {
        this(LREquipmentEffectTypes.EROSION_EFFECT.value(), level);
    }

    public static FinalCalculator DAMAGE_PER_LAYER = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.1F),
                            Constant.of(0.15F)
                    ),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    public static FinalCalculator DAMAGE_PER_LAYER_FULL = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.4F),
                            Constant.of(0.6F)
                    ),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.withering_blade.desc.1", DAMAGE_PER_LAYER.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.withering_blade.desc.2", DAMAGE_PER_LAYER_FULL.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(11184810));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack);
        Erosion erosion = new Erosion(DAMAGE_PER_LAYER.getValue(arg));
        erosion.setSourceEntity(attacker);
        SpecialEffectAPI.addEffectToEntity(target, erosion, Erosion::getFinal);
    }
}
