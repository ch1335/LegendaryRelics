package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record TravelToDimTask(ResourceKey<Level> resourceKey) implements ITask {
    public static MapCodec<TravelToDimTask> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("resourceKey").forGetter(TravelToDimTask::resourceKey)
            ).apply(instance, TravelToDimTask::new)
    );

    @Override
    public boolean check(ITask task) {
        return task instanceof TravelToDimTask(ResourceKey<Level> key) && key == resourceKey;
    }

    @Override
    public MutableComponent getComponent() {
        ResourceLocation location = resourceKey.location();
        return Component.translatable("legendary_relics.task.travel_to_dim", Component.translatable("dimension." + location.getNamespace() + "." + location.getPath())).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public MapCodec<? extends ITask> codec() {
        return CODEC;
    }

    public static TravelToDimTask of(ResourceKey<Level> key) {
        return new TravelToDimTask(key);
    }
}
