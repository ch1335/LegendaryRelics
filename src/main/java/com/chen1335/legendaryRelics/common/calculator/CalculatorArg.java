package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CalculatorArg {
    public static CalculatorArg emptyArg() {
        return new CalculatorArg();
    }

    public static CalculatorArg simpleArg(LivingEntity living) {
        CalculatorArg arg = new CalculatorArg();
        ArgType.THIS_ENTITY.putArg(arg, living);
        return arg;
    }

    public static CalculatorArg simpleArg(LivingEntity living, ItemStack itemStack) {
        CalculatorArg arg = simpleArg(living);
        ArgType.THIS_ITEMS_STACK.putArg(arg, itemStack);
        return arg;
    }

    public static CalculatorArg simpleArg(LivingEntity living, ItemStack itemStack, BaseEffect baseEffect) {
        CalculatorArg arg = simpleArg(living, itemStack);
        ArgType.THIS_EQUIPMENT_EFFECT.putArg(arg, baseEffect);
        return arg;
    }

    private final Map<ArgType<?>, Object> args = new HashMap<>();

    @Nullable
    public <T> T getArg(ArgType<T> arg) {
        return Cast.cast(args.get(arg));
    }

    @NotNull
    public <T> T getArgElse(ArgType<T> arg, T other) {
        T r = Cast.cast(args.get(arg));
        return r == null ? other : r;
    }

    public <T> void putArg(ArgType<T> argType, T arg) {
        args.put(argType, arg);
    }

    public CalculatorArg copy() {
        CalculatorArg arg = new CalculatorArg();
        arg.args.putAll(this.args);
        return arg;
    }

    public static class ArgType<T> {
        public static final ArgType<LivingEntity> THIS_ENTITY = new ArgType<>("this_entity");
        public static final ArgType<ItemStack> THIS_ITEMS_STACK = new ArgType<>("this_items_stack");
        public static final ArgType<BaseEffect> THIS_EQUIPMENT_EFFECT = new ArgType<>("this_equipment_effect");
        private final String name;

        public ArgType(String name) {
            this.name = name;
        }

        public void putArg(CalculatorArg calculatorArg, T arg) {
            calculatorArg.putArg(this, arg);
        }

        @Nullable
        public T getArg(CalculatorArg calculatorArg) {
            return calculatorArg.getArg(this);
        }


        public T getArgOrThrow(CalculatorArg calculatorArg) {
            T arg = calculatorArg.getArg(this);
            if (arg == null) {
                throw new Error("calculatorArg missing arg type:" + name);
            }
            return arg;
        }
    }
}
