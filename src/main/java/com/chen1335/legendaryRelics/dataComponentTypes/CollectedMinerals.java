package com.chen1335.legendaryRelics.dataComponentTypes;

import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CollectedMinerals(List<Block> ores) {

    public static final Codec<CollectedMinerals> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("collected_ores").forGetter(collectedMinerals -> collectedMinerals.ores)
                    )
                    .apply(instance, CollectedMinerals::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectedMinerals> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK).apply(ByteBufCodecs.list()),
            CollectedMinerals::ores,
            CollectedMinerals::new
    );

    public static void checkAndAdd(ItemStack itemStack, Block block) {
        if (BuiltInRegistries.BLOCK.getHolder(BuiltInRegistries.BLOCK.getId(block)).get().is(Tags.Blocks.ORES)) {
            itemStack.update(LRDataComponentTypes.COLLECTED_MINERALS, empty(), block, CollectedMinerals::checkAndAdd);
        }
    }

    public CollectedMinerals checkAndAdd(Block block) {
        if (this.ores.contains(block)) {
            return this;
        }
        List<Block> blocks = new ArrayList<>(this.ores);
        blocks.add(block);
        return new CollectedMinerals(blocks);
    }

    public int getCollectedOresAmount() {
        return ores.size();
    }

    public static CollectedMinerals empty() {
        return new CollectedMinerals(List.of());
    }
}
