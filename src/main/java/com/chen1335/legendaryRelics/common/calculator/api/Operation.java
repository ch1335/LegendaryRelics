package com.chen1335.legendaryRelics.common.calculator.api;

import net.minecraft.network.chat.Style;

public abstract class Operation implements OperationUnit {
    protected Format format = Format.NORMAL;

    public record Format(Style style, float scale, int precision, String format) {
        public static final Format NORMAL = new Format(Style.EMPTY, 1, 1, "%s");
        public static final Format PERCENTAGE = new Format(Style.EMPTY, 100, 1, "%s%%");

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
