package com.chen1335.legendaryRelics.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public class LRColors {
    public enum Component {
        YELLOW(ChatFormatting.YELLOW),
        RED(ChatFormatting.RED),
        BLUE(ChatFormatting.BLUE);
        private final Style style;

        Component(ChatFormatting formatting) {
            this.style = Style.EMPTY.withColor(formatting);
        }

        public Style getStyle() {
            return style;
        }
    }
}
