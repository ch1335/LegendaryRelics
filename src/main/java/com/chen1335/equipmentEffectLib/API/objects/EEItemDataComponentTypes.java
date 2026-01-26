package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EEItemDataComponentTypes {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_DATA = LRDataComponentTypes.DATA_COMPONENTS.register("item_effect_data", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_DATA_ADDITION = LRDataComponentTypes.DATA_COMPONENTS.register("item_effect_data_addition", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SetsEffectBase>> SET_EFFECT = LRDataComponentTypes.DATA_COMPONENTS.register("sets_effect", () -> DataComponentType.<SetsEffectBase>builder().networkSynchronized(SetsEffectBase.STREAM_CODEC).persistent(EERegisterTypes.SETS_EFFECT_TYPE.byNameCodec()).build());

    public static void init() {

    }
}
