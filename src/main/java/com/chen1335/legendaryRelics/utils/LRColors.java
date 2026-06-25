package com.chen1335.legendaryRelics.utils;

public class LRColors {
    public enum Component{
        YELLOW(16777045),
        RED(16733525),
        BLUE(5592575);
        private final int color;

        Component(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
