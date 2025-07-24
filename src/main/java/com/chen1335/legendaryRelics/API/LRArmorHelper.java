package com.chen1335.legendaryRelics.API;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public interface LRArmorHelper extends IEffectEquipment {
    default boolean isEquippedThis(LivingEntity entity) {
        return entity.getItemBySlot(((ArmorItem) this).getEquipmentSlot()).getItem() == this;
    }

    @Nullable
    default ItemStack getEquippedThis(LivingEntity entity) {
        ItemStack itemStack = entity.getItemBySlot(((ArmorItem) this).getEquipmentSlot());
        if (itemStack.getItem() == this) {
            return itemStack;
        }
        return null;
    }

    default void runIfEquippedThis(LivingEntity entity, CalculatorArg arg, BiConsumer<ItemStack, CalculatorArg> consumer) {
        ItemStack itemStack = getEquippedThis(entity);
        if (itemStack != null) {
            CalculatorArg newArg = arg.copy();
            CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(newArg, itemStack);
            consumer.accept(itemStack, newArg);
        }
    }
}
