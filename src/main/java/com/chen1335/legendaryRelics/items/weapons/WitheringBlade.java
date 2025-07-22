package com.chen1335.legendaryRelics.items.weapons;

import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.items.LRSwordItem;
import com.chen1335.legendaryRelics.specialMobEffects.Erosion;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WitheringBlade extends LRSwordItem {

    public WitheringBlade() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.4F)));
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
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack);
        Erosion erosion = new Erosion(DAMAGE_PER_LAYER.getValue(arg));
        erosion.setSourceEntity(attacker);
        SpecialEffectAPI.addEffectToEntity(target, erosion, Erosion::getFinal);
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Player player = null;
        Level level = context.level();
        if (level != null && level.isClientSide) {
            player = LRClient.getClientPlayer();
        }

        CalculatorArg arg = CalculatorArg.simpleArg(player, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.withering_blade.desc.1", DAMAGE_PER_LAYER.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.withering_blade.desc.2", DAMAGE_PER_LAYER_FULL.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(11184810));

    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        if (repair.is(Items.WITHER_ROSE)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
