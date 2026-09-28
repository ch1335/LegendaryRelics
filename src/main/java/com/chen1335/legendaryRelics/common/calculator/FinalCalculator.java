package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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

    /** 未指定样式（即传 {@link Style#EMPTY}）时，展开态数值使用的默认样式。 */
    public static final Style DEFAULT_STYLE = Style.EMPTY.withColor(16777215);

    /** Style.EMPTY 表示"未指定样式"。 */
    private static boolean isUnstyled(Style style) {
        return Style.EMPTY.equals(style);
    }

    /** 施加样式。传 Style.EMPTY 时是空操作，数值会继承上下文样式。 */
    private static MutableComponent styled(MutableComponent component, Style style) {
        return component.withStyle(style);
    }

    @Override
    public MutableComponent toComponent(CalculatorArg calculatorArg) {
        return Component.empty().append(styled(Component.literal(format(getValue(calculatorArg), i)), DEFAULT_STYLE)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg, Style style) {
        return Component.empty().append(styled(Component.literal(format(getValue(calculatorArg), i)), isUnstyled(style) ? DEFAULT_STYLE : style)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
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
        return toComponent(hasShiftDown, calculatorArg, Style.EMPTY);
    }

    public MutableComponent toComponent(boolean hasShiftDown, CalculatorArg calculatorArg, Style style) {
        return toComponent(hasShiftDown, calculatorArg, style, 1);
    }

    public MutableComponent toComponent(boolean hasShiftDown, CalculatorArg calculatorArg, Style style, float scale) {
        return toComponent(hasShiftDown, calculatorArg, style, scale, "%s");
    }

    public MutableComponent toComponent(boolean hasShiftDown, CalculatorArg calculatorArg, Style style, float scale, String format) {
        if (hasShiftDown) {
            MutableComponent value = styled(Component.literal(format.formatted(format(getValue(calculatorArg) * scale, i))), isUnstyled(style) ? DEFAULT_STYLE : style);
            return Component.empty().append(value).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
        } else {
            return styled(Component.literal(format.formatted(format(getValue(calculatorArg) * scale, i))), style);
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
