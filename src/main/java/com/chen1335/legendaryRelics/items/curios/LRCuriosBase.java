package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Map;

public abstract class LRCuriosBase extends Item implements ICurioItem, LRCurio {
    public LRCuriosBase(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return Component.empty().append(Component.translatable("item.legendary_relics.rarity.dark_gold")).append(Component.literal(" ")).append(super.getName(stack));
        }
        return super.getName(stack);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifierMultimap = HashMultimap.create();
        for (Map.Entry<Holder<Attribute>, AttributeModifierHolder> entry : getAttributeModifierHolders().entries()) {
            modifierMultimap.put(entry.getKey(), entry.getValue().toAttributeModifier(slotContext));
        }
        return modifierMultimap;
    }

    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return HashMultimap.create();
    }
}
