package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.world.entity.LivingEntity;
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

    public static class ArgType<T> {
        public static final ArgType<LivingEntity> THIS_ENTITY = new ArgType<>();

        public void putArg(CalculatorArg calculatorArg, T arg) {
            calculatorArg.putArg(this, arg);
        }

        @Nullable
        public T getArg(CalculatorArg calculatorArg) {
            return calculatorArg.getArg(this);
        }
    }
}
