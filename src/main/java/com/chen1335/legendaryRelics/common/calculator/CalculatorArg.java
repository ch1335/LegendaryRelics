package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CalculatorArg {
    public static CalculatorArg emptyArg() {
        return new CalculatorArg();
    }

    private final Map<ArgType<?>, Object> args = new HashMap<>();

    public <T> T getArg(ArgType<T> arg) {
        return (T) args.get(arg);
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
        public static final ArgType<LivingEntity> THIS_ENTITY = new ArgType<>();
        public static final ArgType<ItemStack> THIS_ITEMS_STACK = new ArgType<>();

        public void putArg(CalculatorArg calculatorArg, T arg) {
            calculatorArg.putArg(this, arg);
        }

        @Nullable
        public T getArg(CalculatorArg calculatorArg) {
            return calculatorArg.getArg(this);
        }
    }
}
