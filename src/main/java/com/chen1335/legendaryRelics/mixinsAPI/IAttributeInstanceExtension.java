package com.chen1335.legendaryRelics.mixinsAPI;

import com.chen1335.legendaryRelics.common.attributeFix.fixTypes.BaseFix;

public interface IAttributeInstanceExtension {
    void lr$setValueFix(BaseFix atomicDouble);

    double lr$getTrueValue();
}