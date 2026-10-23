package com.chen1335.legendaryRelics.common.calculator.api;

import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Locale;

public abstract class FormatedOperation implements OperationUnit {
    protected Format format = Format.NORMAL;

    public OperationUnit withFormat(Format format) {
        this.format = format;
        return this;
    }

    public Format getFormat() {
        return format;
    }

    protected MutableComponent formatValue(float value) {
        String text = String.format(Locale.ROOT, format.format(), FinalCalculator.format(value * format.scale(), format.precision()));
        return Component.literal(text).withStyle(format.style());
    }

    public record Format(Style style, float scale, int precision, String format) {

        public static final Format NORMAL = new Format(Style.EMPTY, 1, 1, "%s");
        public static final Format PERCENTAGE = new Format(Style.EMPTY, 100, 1, "%s%%");

        public static final MapCodec<Format> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(Format::style),
                Codec.FLOAT.optionalFieldOf("scale", 1F).forGetter(Format::scale),
                Codec.INT.optionalFieldOf("precision", 1).forGetter(Format::precision),
                Codec.STRING.optionalFieldOf("format", "%s").forGetter(Format::format)
        ).apply(instance, Format::new));

        public static final Codec<Format> CODEC = MAP_CODEC.codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, Format> STREAM_CODEC = StreamCodec.composite(
                Style.Serializer.TRUSTED_STREAM_CODEC,
                Format::style,
                ByteBufCodecs.FLOAT,
                Format::scale,
                ByteBufCodecs.INT,
                Format::precision,
                ByteBufCodecs.STRING_UTF8,
                Format::format,
                Format::new
        );

        public Builder unwrap() {
            return new Builder(style, scale, precision, format);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Style style = Style.EMPTY;
            private float scale = 1;
            private int precision = 1;
            private String format = "%s";

            public Builder() {
            }

            protected Builder(Style style, float scale, int precision, String format) {
                this.style = style;
                this.scale = scale;
                this.precision = precision;
                this.format = format;
            }

            public Builder style(Style style) {
                this.style = style;
                return this;
            }

            public Builder scale(float scale) {
                this.scale = scale;
                return this;
            }

            public Builder precision(int precision) {
                this.precision = precision;
                return this;
            }

            public Builder format(String format) {
                this.format = format;
                return this;
            }

            public Format build() {
                return new Format(style, scale, precision, format);
            }
        }
    }
}
