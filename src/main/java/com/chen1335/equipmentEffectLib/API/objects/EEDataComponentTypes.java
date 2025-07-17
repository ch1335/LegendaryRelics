package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EEDataComponentTypes {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_DATA = LRDataComponentTypes.DATA_COMPONENTS.register("item_effect_data", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());

    public static void init() {

    }
}
