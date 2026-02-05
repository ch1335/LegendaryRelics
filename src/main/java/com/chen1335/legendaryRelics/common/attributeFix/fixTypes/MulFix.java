package com.chen1335.legendaryRelics.common.attributeFix.fixTypes;

public class MulFix extends BaseFix {
    private final float mul;

    public MulFix(float mul) {
        this.mul = mul;
    }

    @Override
    public double getValue(double original) {
        return original * mul;
    }
}
