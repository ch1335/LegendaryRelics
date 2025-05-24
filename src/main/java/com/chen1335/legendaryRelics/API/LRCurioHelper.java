package com.chen1335.legendaryRelics.API;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface LRCurioHelper {
    default boolean isEquippedThis(LivingEntity entity) {
        Optional<ICuriosItemHandler> o = CuriosApi.getCuriosInventory(entity);
        return o.filter(iCuriosItemHandler -> !iCuriosItemHandler.findCurios((Item) this).isEmpty()).isPresent();
    }

    default ItemStack getEquippedThis(LivingEntity entity) {
        Optional<ICuriosItemHandler> o = CuriosApi.getCuriosInventory(entity);
        if (o.isPresent()) {
            List<SlotResult> list = o.get().findCurios((Item) this);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.getFirst().stack();
            }
        }
        return null;
    }

    default void runIfEquippedThis(LivingEntity entity, CalculatorArg arg, BiConsumer<ItemStack, CalculatorArg> consumer) {
        ItemStack itemStack = getEquippedThis(entity);
        if (itemStack != null) {
            CalculatorArg newArg = arg.copy();
            CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(newArg, itemStack);
            CalculatorArg.ArgType.THIS_ENTITY.putArg(newArg, entity);
            consumer.accept(itemStack, newArg);
        }
    }
    default void runIfEquippedThis(LivingEntity entity, BiConsumer<ItemStack, CalculatorArg> consumer) {
        ItemStack itemStack = getEquippedThis(entity);
        if (itemStack != null) {
            CalculatorArg newArg = CalculatorArg.emptyArg();
            CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(newArg, itemStack);
            CalculatorArg.ArgType.THIS_ENTITY.putArg(newArg, entity);
            consumer.accept(itemStack, newArg);
        }
    }
}
