package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.FlyingKnife;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class SkeletonThrowingKnife extends LRMeleeWeaponBase {

    @Calculator
    public static final FinalCalculator DAMAGE = FinalCalculator.of(
            Add.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(2),
                            Constant.of(3)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.3F),
                                    Constant.of(0.5F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    )
            )
    );

    public SkeletonThrowingKnife() {
        super(Tier.Weapon.SKELETON_THROWING_KNIFE, new Item.Properties().rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg calculatorArg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(calculatorArg, tooltipFlag);
            tooltipComponents.add(Component.translatable("item.legendary_relics.skeleton_throwing_knife.desc.1", ComponentHolders.Combat.physicalDamage(DAMAGE.toComponent(calculatorArg))).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("legendary_relics.cooldown_affected_by_attack_speed").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);
        FlyingKnife flyingKnife = new FlyingKnife(player, level, itemInHand);
        flyingKnife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1, 1);
        flyingKnife.setPos(player.getEyePosition());
        flyingKnife.setBaseDamage(DAMAGE.getValue(CalculatorArg.simpleArg(player,itemInHand)));
        level.addFreshEntity(flyingKnife);
        player.getCooldowns().addCooldown(itemInHand.getItem(), (int) (20 / player.getAttributeValue(Attributes.ATTACK_SPEED)));
        itemInHand.hurtAndBreak(1, player, usedHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return InteractionResultHolder.success(itemInHand);
    }

    @Override
    public boolean isNoRightClickCooldown() {
        return true;
    }
}