package com.chen1335.legendaryRelics.misc.gameTask.taskTypes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public record GainItemTask(Item item) implements ITask {
    public static MapCodec<GainItemTask> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(GainItemTask::item)
            ).apply(instance, GainItemTask::new)
    );

    @Override
    public boolean check(ITask task) {
        return task instanceof GainItemTask(Item item1) && item1 == this.item;
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable("legendary_relics.task.gain_item", item.getDescription()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public MapCodec<? extends ITask> codec() {
        return CODEC;
    }

    public static GainItemTask of(Item item) {
        return new GainItemTask(item);
    }
}
