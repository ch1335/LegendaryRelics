package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.LegendaryRelics;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Map;

public class HardenedRing extends LRCuriosBase {

    public HardenedRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(2, AttributeModifier.Operation.ADD_VALUE)
    );
    public static FinalCalculator ARMOR_AMOUNT = FinalCalculator.of(
            Add.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(2F),
                            Constant.of(4F)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.50F),
                                    Constant.of(0.75F)
                            ),
                            EntityAttributeValue.of(Attributes.ARMOR)
                    )
            )
    );
    public static FinalCalculator TIME_KEEP = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(5),
            Constant.of(7.5F)
    ));
    public static FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(15),
            Constant.of(12.5F)
    ));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.hardened_ring.skill",
                    ARMOR_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                    TIME_KEEP.toComponent(tooltipFlag.hasShiftDown(), args),
                    COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.hardened_ring.skill",
                    ARMOR_AMOUNT.toRawComponent(),
                    TIME_KEEP.toRawComponent(),
                    COOLDOWN.toRawComponent()
            ).withColor(0xaeaeae));
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap = HashMultimap.create();
        for (Map.Entry<Holder<Attribute>, AttributeModifierHolder> entry : ATTRIBUTE_MODIFIERS.entries()) {
            modifierMultimap.put(entry.getKey(), entry.getValue().toAttributeModifier(slotContext));
        }
        return modifierMultimap;
    }

    @Override
    public void handleLivingDamageEventLowest(LivingDamageEvent.Post event, CalculatorArg calculatorArg, ItemStack itemStack) {
        if (event.getEntity() instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(LRItems.HARDENED_RING.get())) {
                player.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                        .addAttributeModifier(
                                player,
                                Attributes.ARMOR,
                                new AttributeModifier(
                                        LegendaryRelics.id("hardened_ring_armor"),
                                        HardenedRing.ARMOR_AMOUNT.getValue(calculatorArg),
                                        AttributeModifier.Operation.ADD_VALUE
                                ),
                                HardenedRing.TIME_KEEP.getInt(calculatorArg) * 20
                        );
                player.getCooldowns().addCooldown(LRItems.HARDENED_RING.get(), HardenedRing.COOLDOWN.getInt(calculatorArg) * 20);
            }
        }
    }
}
