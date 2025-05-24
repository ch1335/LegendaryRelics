package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.google.common.base.Suppliers;

import java.util.Set;
import java.util.function.Supplier;

public class TestCombineCurio extends CombineCurio {
    public Supplier<Set<LRCurio>> subCurios = Suppliers.memoize(() -> Set.of(
            LRItems.SACRED_TALISMAN.value(),
            LRItems.HARDENED_RING.value()
    ));

    public TestCombineCurio() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public Set<LRCurio> getCombinedCurios() {
        return subCurios.get();
    }
}
