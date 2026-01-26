package com.chen1335.legendaryRelics.common.attributeFix.fixTypes;

public class ConstantValueFix extends BaseFix{
    private final double constant;

    public ConstantValueFix(double constant){
        this.constant = constant;
    }

    @Override
    public double getValue(double original) {
        return constant;
    }
}
