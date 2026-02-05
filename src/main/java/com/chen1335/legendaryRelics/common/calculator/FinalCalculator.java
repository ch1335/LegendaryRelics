package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.math.BigDecimal;

public class FinalCalculator implements Unit {

    public static final StreamCodec<RegistryFriendlyByteBuf, FinalCalculator> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.unit,
            ByteBufCodecs.INT,
            value -> value.i,
            FinalCalculator::new
    );


    private final Unit defaultUnit;
    private Unit unit;
    private int i;

    private FinalCalculator(Unit unit, int i) {
        this.unit = unit;
        this.defaultUnit = unit;
        this.i = i;
    }

    public FinalCalculator define(FinalCalculator finalCalculator) {
        this.unit = finalCalculator.unit;
        this.i = finalCalculator.i;
        return this;
    }

    public FinalCalculator define(Unit unit) {
        this.unit = unit;
        return this;
    }

    public FinalCalculator define(Unit unit, int i) {
        FinalCalculator calculator = define(unit);
        calculator.i = i;
        return calculator;
    }

    public boolean changed() {
        return unit != defaultUnit;
    }


    public void reset() {
        unit = defaultUnit;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return unit.getValue(calculatorArg);
    }

    public int getInt(CalculatorArg calculatorArg) {
        return (int) getValue(calculatorArg);
    }

    @Override
    public MutableComponent toComponent(CalculatorArg calculatorArg) {
        return Component.empty().append(Component.literal(format(getValue(calculatorArg), i)).withColor(16777215)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg, int color) {
        return Component.empty().append(Component.literal(format(getValue(calculatorArg), i)).withColor(color)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }

    public Component toRawComponent() {
        return Component.empty().append("(").append(unit.toComponent(CalculatorArg.emptyArg())).append(")").withColor(5592405);
    }


    public Component toRawComponent(CalculatorArg calculatorArg) {
        return Component.empty().append("(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
    }

    public MutableComponent toComponent(boolean hasShiftDown, CalculatorArg calculatorArg) {
        return toComponent(hasShiftDown, calculatorArg, -1);
    }

    public MutableComponent toComponent(boolean hasShiftDown, CalculatorArg calculatorArg, int color) {
        if (hasShiftDown) {
            if (color == -1) {
                color = 16777215;
            }
            return Component.empty().append(Component.literal(format(getValue(calculatorArg), i)).withColor(color)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
        } else {
            MutableComponent component = Component.literal(format(getValue(calculatorArg), i));
            if (color != -1) {
                component.withColor(color);
            }
            return component;
        }
    }

    public MutableComponent toPercentageComponent(boolean hasShiftDown, CalculatorArg calculatorArg) {
        return toPercentageComponent(hasShiftDown, calculatorArg, -1);
    }

    public MutableComponent toPercentageComponent(boolean hasShiftDown, CalculatorArg calculatorArg, int color) {
        if (hasShiftDown) {
            if (color == -1) {
                color = 16777215;
            }
            return Component.empty().append(Component.literal(format(getValue(calculatorArg) * 100, i) + "%").withColor(color)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
        } else {
            MutableComponent component = Component.literal(format(getValue(calculatorArg) * 100, i) + "%");
            if (color != -1) {
                component.withColor(color);
            }
            return component;
        }
    }

    public static FinalCalculator of(Unit unit) {
        return new FinalCalculator(unit, 2);
    }

    public static FinalCalculator of(Unit unit, int i) {
        return new FinalCalculator(unit, i);
    }

    public static String format(float value, int i) {
        return new BigDecimal(String.format("%." + i + "f", value)).stripTrailingZeros().toPlainString();
    }

    public static double castToDoubleStrict(float f) {
        return new BigDecimal(String.valueOf(f)).doubleValue();
    }
}
