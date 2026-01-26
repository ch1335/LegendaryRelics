package com.chen1335.legendaryRelics.mixinsAPI;

import com.chen1335.legendaryRelics.common.attributeFix.fixTypes.BaseFix;
import com.google.common.util.concurrent.AtomicDouble;

public interface IAttributeInstanceMixin {
    void lr$setValueFix(BaseFix atomicDouble);

    double lr$getTrueValue();
}