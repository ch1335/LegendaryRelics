package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface ITask {
    boolean check(ITask task);

    MutableComponent getComponent();

    MapCodec<? extends ITask> codec();

    class Tasks {
        public static final HashBiMap<ResourceLocation, MapCodec<? extends ITask>> NAME_TO_CODEC = HashBiMap.create();
        public static Codec<ITask> CODEC;

        static {
            Codec<MapCodec<? extends ITask>> xmap = ResourceLocation.CODEC.xmap(NAME_TO_CODEC::get, codec -> NAME_TO_CODEC.inverse().get(codec));
            CODEC = xmap.dispatch(ITask::codec, Function.identity());

            register("custom_task", CustomTask.CODEC);
            register("gain_item", GainItemTask.CODEC);
            register("kill_entity", KillEntityTask.CODEC);
            register("travel_to_dim", TravelToDimTask.CODEC);
        }

        private static void register(String name,MapCodec<? extends ITask> mapCodec){
            NAME_TO_CODEC.put(LegendaryRelics.id(name), mapCodec);
        }
    }
}
