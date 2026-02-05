package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.LRArmorHelper;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LRArmorBase extends ArmorItem implements LRArmorHelper {
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public LRArmorBase(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        defaultModifiers = Suppliers.memoize(this::buildAttributes);
    }


    protected ItemAttributeModifiers buildAttributes() {
        return super.getDefaultAttributeModifiers();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return defaultModifiers.get();
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return Component.empty().append(Component.translatable("item.legendary_relics.rarity.dark_gold")).append(Component.literal(" ")).append(super.getName(stack));
        }
        return super.getName(stack);
    }
}
