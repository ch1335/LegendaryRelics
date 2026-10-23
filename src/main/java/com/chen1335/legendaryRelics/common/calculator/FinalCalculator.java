package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class FinalCalculator extends FormatedOperation {

    public static final MapCodec<FinalCalculator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.fieldOf("unit").forGetter(calculator -> calculator.unit),
            Format.CODEC.optionalFieldOf("format", Format.NORMAL).forGetter(calculator -> calculator.format)
    ).apply(instance, FinalCalculator::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FinalCalculator> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.unit,
            Format.STREAM_CODEC,
            value -> value.format,
            FinalCalculator::new
    );

    /**
     * 未指定样式（即传 {@link Style#EMPTY}）时，展开态数值使用的默认样式。
     */
    public static final Style DEFAULT_STYLE = Style.EMPTY.withColor(16777215);

    private final OperationUnit defaultUnit;
    private OperationUnit unit;

    private FinalCalculator(OperationUnit unit, Format format) {
        this.unit = unit;
        this.defaultUnit = unit;
        this.format = format;
    }

    public FinalCalculator define(FinalCalculator finalCalculator) {
        this.unit = finalCalculator.unit;
        this.format = finalCalculator.format;
        return this;
    }

    public FinalCalculator define(OperationUnit unit) {
        this.unit = unit;
        return this;
    }

    public boolean changed() {
        return unit != defaultUnit;
    }

    public void reset() {
        unit = defaultUnit;
    }

    @Override
    public FinalCalculator withFormat(Format format) {
        super.withFormat(format);
        return this;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return unit.getValue(calculatorArg);
    }

    public int getInt(CalculatorArg calculatorArg) {
        return (int) getValue(calculatorArg);
    }

    private static boolean isUnstyled(Style style) {
        return Style.EMPTY.equals(style);
    }

    @Override
    public MutableComponent expand(CalculatorArg calculatorArg) {
        MutableComponent value = formatResult(calculatorArg, format, true);
        return Component.empty().append(value).append("=(").append(unit.expand(calculatorArg)).append(")").withColor(5592405);
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg) {

        return toComponent(calculatorArg, Style.EMPTY);
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg, Style style) {
        return toComponent(calculatorArg, style, 1, "%s");
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg, Style style, float scale) {
        return toComponent(calculatorArg, style, scale, "%s");
    }

    public MutableComponent toComponent(CalculatorArg calculatorArg, Style style, float scale, String format) {
        Format resultFormat = resolveFormat(style, scale, format);
        boolean hasShiftDown = CalculatorArg.ArgType.TOOLTIP_FLAG.getOrDefault(calculatorArg, TooltipFlag.NORMAL).hasShiftDown();
        MutableComponent value = formatResult(calculatorArg, resultFormat, hasShiftDown);
        if (hasShiftDown) {
            return Component.empty().append(value).append("=(").append(unit.expand(calculatorArg)).append(")").withColor(5592405);
        }
        return value;
    }

    private Format resolveFormat(Style style, float scale, String format) {
        Format.Builder builder = this.format.unwrap();
        if (!isUnstyled(style)) {
            builder.style(style);
        }
        if (scale != 1F) {
            builder.scale(scale);
        }
        if (!"%s".equals(format)) {
            builder.format(format);
        }
        return builder.build();
    }

    private MutableComponent formatResult(CalculatorArg calculatorArg, Format resultFormat, boolean expanded) {
        String text = String.format(Locale.ROOT, resultFormat.format(), format(getValue(calculatorArg) * resultFormat.scale(), resultFormat.precision()));
        Style style = resultFormat.style();
        if (expanded && isUnstyled(style)) {
            style = DEFAULT_STYLE;
        }
        return Component.literal(text).withStyle(style);
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
        return Component.empty().append("(").append(unit.expand(CalculatorArg.emptyArg())).append(")").withColor(5592405);
    }


    public Component toRawComponent(CalculatorArg calculatorArg) {
        return Component.empty().append("(").append(unit.expand(calculatorArg)).append(")").withColor(5592405);
    }

    public static FinalCalculator of(OperationUnit unit) {
        return new FinalCalculator(unit, Format.NORMAL);
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
