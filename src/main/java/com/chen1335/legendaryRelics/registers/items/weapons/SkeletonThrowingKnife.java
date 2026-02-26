package com.chen1335.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingKnife;
import com.chen1335.legendaryRelics.registers.items.LRSwordItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class SkeletonThrowingKnife extends LRSwordItem {
    public static Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 512, 6.0F, 5.0F, 14, () -> Ingredient.of(Tags.Items.BONES));

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

    public SkeletonThrowingKnife(Properties properties) {
        super(TIER, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg calculatorArg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            tooltipComponents.add(Component.translatable("item.legendary_relics.skeleton_throwing_knife.desc.1", DAMAGE.toComponent(tooltipFlag.hasShiftDown(), calculatorArg)).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("legendary_relics.cooldown_affected_by_attack_speed").withStyle(ChatFormatting.DARK_GRAY));

        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return SwordItem.createAttributes(SkeletonThrowingKnife.TIER, 0, -2).withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(LegendaryRelics.id("skeleton_throwing_knife"), -1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND);
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
