package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public abstract class CombineCurio extends LRCuriosBase {

    public CombineCurio(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        getCombinedCurios().forEach(lrCurio -> {
            ((Item) lrCurio).appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        });
    }

    abstract public Set<LRCurio> getCombinedCurios();
}
