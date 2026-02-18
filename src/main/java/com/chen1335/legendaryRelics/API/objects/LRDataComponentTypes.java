package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.dataComponentTypes.BowUsingArrow;
import com.chen1335.legendaryRelics.dataComponentTypes.CollectedMinerals;
import com.chen1335.legendaryRelics.dataComponentTypes.ItemTask;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRDataComponentTypes {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, LegendaryRelics.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ANCIENT_FRAGMENT_UPDATED = DATA_COMPONENTS.register("ancient_fragment_updated", () -> DataComponentType.<Boolean>builder().networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CollectedMinerals>> COLLECTED_MINERALS = DATA_COMPONENTS.register("collected_minerals", () -> DataComponentType.<CollectedMinerals>builder().networkSynchronized(CollectedMinerals.STREAM_CODEC).persistent(CollectedMinerals.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemTask>> ITEM_TASK = DATA_COMPONENTS.register("item_task", () -> DataComponentType.<ItemTask>builder().networkSynchronized(ItemTask.STREAM_CODEC).persistent(ItemTask.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BowUsingArrow>> BOW_USING_ARROW = DATA_COMPONENTS.register("bow_using_arrow", () -> DataComponentType.<BowUsingArrow>builder().networkSynchronized(BowUsingArrow.STREAM_CODEC).persistent(BowUsingArrow.CODEC).build());

}
