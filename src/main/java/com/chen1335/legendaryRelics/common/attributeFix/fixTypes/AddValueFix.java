package com.chen1335.legendaryRelics.common.attributeFix.fixTypes;

public class AddValueFix extends BaseFix{
   private final double add;
    public AddValueFix(double add){
        this.add = add;
    }
    @Override
    public double getValue(double original) {
        return original +add;
    }
}
