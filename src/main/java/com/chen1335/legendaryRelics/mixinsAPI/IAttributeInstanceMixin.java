package com.chen1335.legendaryRelics.mixinsAPI;

import com.google.common.util.concurrent.AtomicDouble;

public interface IAttributeInstanceMixin {
    void lr$setValueFix(AtomicDouble atomicDouble);

    double lr$getTrueValue();
}