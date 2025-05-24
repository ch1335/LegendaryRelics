package com.chen1335.legendaryRelics.API;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.items.curios.CombineCurio;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    static void runForeachCurio(LivingEntity entity, TriConsumer<LRCurio, CalculatorArg, ItemStack> consumer) {
        HashSet<LRCurio> completed = new HashSet<>();
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ENTITY.putArg(args, entity);
        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            for (ICurioStacksHandler value : iCuriosItemHandler.getCurios().values()) {
                for (int i = 0; i < value.getSlots(); i++) {
                    ItemStack itemStack = value.getStacks().getStackInSlot(i);
                    if (itemStack.getItem() instanceof LRCurio lrCurio && !completed.contains(itemStack.getItem())) {
                        CalculatorArg newArgs = args.copy();
                        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(newArgs, itemStack);
                        consumer.accept(lrCurio, newArgs, itemStack);
                        completed.add(lrCurio);
                        if (lrCurio instanceof CombineCurio combineCurio) {
                            innerRunForeachCurio(completed, combineCurio.getCombinedCurios(), itemStack, newArgs, consumer);
                        }
                    }
                }
            }
        });
    }

    static void innerRunForeachCurio(HashSet<LRCurio> completed, Set<LRCurio> curios, ItemStack itemStack, CalculatorArg arg, TriConsumer<LRCurio, CalculatorArg, ItemStack> consumer) {
        for (LRCurio curio : curios) {
            if (!completed.contains(curio)) {
                consumer.accept(curio, arg, itemStack);
                completed.add(curio);
                if (curio instanceof CombineCurio combineCurio) {
                    innerRunForeachCurio(completed, combineCurio.getCombinedCurios(), itemStack, arg, consumer);
                }
            }
        }
    }
}
