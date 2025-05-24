package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;

public class DarkSteelClaw extends LRCuriosBase {
    public DarkSteelClaw() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(4, AttributeModifier.Operation.ADD_VALUE),
            Attributes.ATTACK_DAMAGE, new AttributeModifierHolder(1, AttributeModifier.Operation.ADD_VALUE)
    );

    public static FinalCalculator DAMAGE_ADD = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.15F),
                            Constant.of(0.2F)
                    ),
                    EntityAttributeValue.of(Attributes.ARMOR)
            )
    );

    public static void handleAttack(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack) {
        event.setAmount(event.getAmount() + DAMAGE_ADD.getValue(newArgs));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.dark_steel_claw.skill",
                    DAMAGE_ADD.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.dark_steel_claw.skill",
                    DAMAGE_ADD.toRawComponent()
            ).withColor(0xaeaeae));
        }

        tooltipComponents.add(Component.translatable("legendary_relics.items.skill.cannot_be_tacked").withColor(5592405));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap = HashMultimap.create();
        for (Map.Entry<Holder<Attribute>, AttributeModifierHolder> entry : ATTRIBUTE_MODIFIERS.entries()) {
            modifierMultimap.put(entry.getKey(), entry.getValue().toAttributeModifier(slotContext));
        }
        return modifierMultimap;
    }
}
