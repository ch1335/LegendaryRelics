package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurioHelper;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class LRCuriosBase extends Item implements ICurioItem, LRCurioHelper {
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
}
