package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;

public record KillEntityTask(EntityType<?> entityType) implements ITask {
    public static MapCodec<KillEntityTask> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(KillEntityTask::entityType)
            ).apply(instance, KillEntityTask::new)
    );

    @Override
    public boolean check(ITask task) {
        return task instanceof KillEntityTask killEntityTask && killEntityTask.entityType == entityType;
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable("legendary_relics.task.kill_entity", entityType.getDescription()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public MapCodec<? extends ITask> codec() {
        return CODEC;
    }

    public static KillEntityTask of(EntityType<?> type) {
        return new KillEntityTask(type);
    }
}
