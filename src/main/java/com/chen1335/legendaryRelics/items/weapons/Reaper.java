package com.chen1335.legendaryRelics.items.weapons;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.entities.projectiles.misc.FlyingReaper;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.SoulEater;
import com.chen1335.legendaryRelics.items.LRSwordItem;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Reaper extends LRSwordItem {
    public Reaper() {
        super(Tiers.NETHERITE, new Properties().stacksTo(1).rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 8, -2.4F)));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new SoulEater(1));
    }

    @Calculator
    public static FinalCalculator DAMAGE = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(1.2F),
                            Constant.of(1.5F)
                    ),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg calculatorArg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            tooltipComponents.add(Component.translatable("item.legendary_relics.reaper.desc.1", DAMAGE.toComponent(tooltipFlag.hasShiftDown(), calculatorArg)).withColor(0xaeaeae));
            tooltipComponents.add(Component.translatable("item.legendary_relics.reaper.desc.2").withColor(5592405));
            tooltipComponents.add(Component.translatable("item.legendary_relics.reaper.desc.3").withColor(5592405));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (player.getCooldowns().isOnCooldown(LRItems.REAPER.asItem())) {
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }
        CalculatorArg calculatorArg = CalculatorArg.simpleArg(player, player.getItemInHand(usedHand));
        FlyingReaper flyingReaper = new FlyingReaper(level, player, player.getItemInHand(usedHand), DAMAGE.getValue(calculatorArg));
        flyingReaper.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1F);
        level.addFreshEntity(flyingReaper);
        player.swing(usedHand, true);
        level.playSound(null, player, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(LRItems.REAPER.asItem(), 20);

        return super.use(level, player, usedHand);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        if (repair.is(Items.OBSIDIAN)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
