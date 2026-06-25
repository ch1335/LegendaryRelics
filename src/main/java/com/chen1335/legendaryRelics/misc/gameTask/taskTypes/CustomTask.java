package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record CustomTask(ResourceLocation location) implements ITask {
    public static MapCodec<CustomTask> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.flatXmap(s -> DataResult.success(ResourceLocation.parse(s)), resourceLocation -> DataResult.success(resourceLocation.toString())).fieldOf("location").forGetter(CustomTask::location)
            ).apply(instance, CustomTask::new)
    );

    @Override
    public boolean check(ITask task) {
        return task instanceof CustomTask(ResourceLocation location1) && location1.equals(location);
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable("%s.task.custom.%s".formatted(location.getNamespace(), location.getPath())).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public MapCodec<? extends ITask> codec() {
        return CODEC;
    }

    public static CustomTask of(String name) {
        return new CustomTask(LegendaryRelics.id(name));
    }
}
