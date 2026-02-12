package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

public interface ITask {
    boolean check(ITask task);

    MutableComponent getComponent();

    MapCodec<? extends ITask> codec();

    class Tasks {
        public static final HashBiMap<String, MapCodec<? extends ITask>> NAME_TO_CODEC = HashBiMap.create();
        public static Codec<ITask> CODEC;

        static {
            Codec<MapCodec<? extends ITask>> xmap = Codec.STRING.xmap(NAME_TO_CODEC::get, codec -> NAME_TO_CODEC.inverse().get(codec));
            CODEC = xmap.dispatch(ITask::codec, Function.identity());

            NAME_TO_CODEC.put("custom_task", CustomTask.CODEC);
            NAME_TO_CODEC.put("gain_item", GainItemTask.CODEC);
            NAME_TO_CODEC.put("kill_entity", KillEntityTask.CODEC);
            NAME_TO_CODEC.put("travel_to_dim", TravelToDimTask.CODEC);
        }
    }
}
