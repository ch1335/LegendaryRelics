package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurioHelper;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Map;

public class HardenedRing extends Item implements ICurioItem, LRCurioHelper {

    public HardenedRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(2, AttributeModifier.Operation.ADD_VALUE)
    );
    public static FinalCalculator ARMOR_AMOUNT = FinalCalculator.of(
            Add.of(
                    Constant.of(2),
                    Mul.of(
                            Constant.of(0.75F),
                            EntityAttributeValue.of(Attributes.ARMOR)
                    )
            )
    );
    public static FinalCalculator TIME_KEEP = FinalCalculator.of(Constant.of(5));
    public static FinalCalculator COOLDOWN = FinalCalculator.of(Constant.of(15));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = CalculatorArg.emptyArg();
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
}
