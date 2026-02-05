package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EEItemDataComponentTypes {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_DATA = LRDataComponentTypes.DATA_COMPONENTS.register("item_effect_data", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_DATA_ADDITION = LRDataComponentTypes.DATA_COMPONENTS.register("item_effect_data_addition", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SetEffect>> SET_EFFECT = LRDataComponentTypes.DATA_COMPONENTS.register("sets_effect", () -> DataComponentType.<SetEffect>builder().networkSynchronized(SetEffect.STREAM_CODEC).persistent(EERegisterTypes.SETS_EFFECT_TYPE.byNameCodec()).build());

    public static void init() {

    }
}
