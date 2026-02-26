package com.chen1335.legendaryRelics.registers.dataComponentTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record ItemTask(UUID taskId) {
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ItemTask> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of((buffer, value) -> buffer.writeUUID(value), buffer -> buffer.readUUID()),
            ItemTask::taskId,
            ItemTask::new
    );
    public static final Codec<ItemTask> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.flatXmap(s -> DataResult.success(UUID.fromString(s)), uuid -> DataResult.success(uuid.toString())).fieldOf("taskId").forGetter(ItemTask::taskId)
                    )
                    .apply(instance, ItemTask::new)
    );


}
