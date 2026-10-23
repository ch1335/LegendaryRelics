package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class FinalCalculator implements OperationUnit {

    public static final MapCodec<FinalCalculator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.fieldOf("unit").forGetter(calculator -> calculator.unit),
            Codec.INT.optionalFieldOf("precision", 2).forGetter(calculator -> calculator.i)
    ).apply(instance, FinalCalculator::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FinalCalculator> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.unit,
            ByteBufCodecs.INT,
            value -> value.i,
            FinalCalculator::new
    );


    private final OperationUnit defaultUnit;
    private OperationUnit unit;
    private int i;

    private FinalCalculator(OperationUnit unit, int i) {
        this.unit = unit;
        this.defaultUnit = unit;
        this.i = i;
    }

    public FinalCalculator define(FinalCalculator finalCalculator) {
        this.unit = finalCalculator.unit;
        this.i = finalCalculator.i;
        return this;
    }

    public FinalCalculator define(OperationUnit unit) {
        this.unit = unit;
        return this;
    }

    public FinalCalculator define(OperationUnit unit, int i) {
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
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<? extends OperationUnit> getCodec() {
        return CODEC;
    }

    @Override
    public OperationType getType() {
        throw new UnsupportedOperationException();
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
        // format 由调用方提供，可能含 %f 之类的数字占位符，所以同样固定用 Locale.ROOT
        if (hasShiftDown) {
            MutableComponent value = styled(Component.literal(String.format(Locale.ROOT, format, format(getValue(calculatorArg) * scale, i))), isUnstyled(style) ? DEFAULT_STYLE : style);
            return Component.empty().append(value).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
        } else {
            return styled(Component.literal(String.format(Locale.ROOT, format, format(getValue(calculatorArg) * scale, i))), style);
        }
    }

    public static FinalCalculator of(OperationUnit unit) {
        return new FinalCalculator(unit, 2);
    }

    public static FinalCalculator of(OperationUnit unit, int i) {
        return new FinalCalculator(unit, i);
    }

    public static String format(float value, int i) {
        if (!Float.isFinite(value)) {
            return Float.toString(value);
        }
        return new BigDecimal(Float.toString(value))
                .setScale(i, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static double castToDoubleStrict(float f) {
        return new BigDecimal(String.valueOf(f)).doubleValue();
    }
}
