package com.chen1335.legendaryRelics.common.lootModifier;

import com.chen1335.legendaryRelics.config.LootConfig;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class LootEntry {
    private final String id;
    public DefaultValueHolder<Double> chance;
    public DefaultValueHolder<Integer> rolls = new DefaultValueHolder<>(1);
    public DefaultValueHolder<List<String>> lootTables;
    public final Consumer<ObjectArrayList<ItemStack>> stackGetter;
    public boolean isCreatedByRemote = false;
    public static final StreamCodec<ByteBuf, LootEntry> STREAM_CODEC = StreamCodec.of((byteBuf, lootEntry) -> {
        ByteBufCodecs.STRING_UTF8.encode(byteBuf, lootEntry.id);
        byteBuf.writeDouble(lootEntry.chance.value);
        byteBuf.writeInt(lootEntry.rolls.value);
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(byteBuf, lootEntry.lootTables.value);
    }, byteBuf -> {
        String id = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
        double chance = byteBuf.readDouble();
        int rolls = byteBuf.readInt();
        List<String> lootTables = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(byteBuf);
        LootEntry lootEntry = LootModifier.LOOT_ENTRIES.get(id);
        if (lootEntry == null) {
            return new LootEntry(id, chance, lootTables, itemStacks -> {
            }).rolls(rolls).markCreatedByRemote();
        }
        lootEntry.chance.value = chance;
        lootEntry.rolls.value = rolls;
        lootEntry.lootTables.value = lootTables;
        return lootEntry;
    });

    private LootEntry markCreatedByRemote() {
        isCreatedByRemote = true;
        return this;
    }

    public LootEntry(String id, double chance, List<String> lootTables, Consumer<ObjectArrayList<ItemStack>> consumer) {
        this.id = id;
        this.chance = new DefaultValueHolder<>(Double.parseDouble(String.format("%.2f", chance)));
        this.lootTables = new DefaultValueHolder<>(lootTables, List.copyOf(lootTables));
        this.stackGetter = consumer;
        LootModifier.LOOT_ENTRIES.put(id, this);
    }

    public LootEntry rolls(int rolls) {
        this.rolls = new DefaultValueHolder<>(rolls);
        return this;
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();

        for (String resourceLocation : lootTables.value) {
            listTag.add(StringTag.valueOf(resourceLocation));
        }
        compoundTag.putDouble("chance", chance.value);
        compoundTag.putDouble("rolls", rolls.value);
        compoundTag.put("lootTables", listTag);
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        chance.value = compoundTag.getDouble("chance");
        rolls.value = compoundTag.getInt("rolls");
        ListTag listTag = compoundTag.getList("lootTables", Tag.TAG_STRING);
        List<String> resourceLocations = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            resourceLocations.add(listTag.getString(i));
        }
        lootTables.value = resourceLocations;
    }

    public Component getComponent() {
        return Component.translatable("legendary_relics.loot_config." + id);
    }

    public static LootEntry of(String id, double chance, List<String> lootTables, Consumer<ObjectArrayList<ItemStack>> consumer) {
        return new LootEntry(id, chance, lootTables, consumer);
    }

    public static LootEntry ofSpecialLoot(String id, double chance, List<ResourceLocation> lootTables, Supplier<? extends Item> itemSupplier) {
        return of(id, chance, LootConfig.fromResourceLocationList(lootTables), itemStacks -> itemStacks.add(itemSupplier.get().getDefaultInstance()));
    }


    public static class SimpleItemLoot extends LootEntry {
        public Supplier<? extends Item> itemSupplier;

        public SimpleItemLoot(String id, double chance, List<String> lootTables, Supplier<? extends Item> itemSupplier) {
            super(id, chance, lootTables, itemStacks -> itemStacks.add(itemSupplier.get().getDefaultInstance()));
            this.itemSupplier = itemSupplier;
        }

        @Override
        public Component getComponent() {
            return itemSupplier.get().getDescription();
        }
    }

    public static LootEntry ofSimpleItem(String id, double chance, List<String> lootTables, Supplier<? extends Item> itemSupplier) {
        return new SimpleItemLoot(BuiltInRegistries.ITEM.getKey(itemSupplier.get()).toString(), chance, lootTables, itemSupplier);
    }

    public static LootEntry ofSimpleItem(double chance, List<ResourceLocation> lootTables, DeferredItem<? extends Item> deferredItem) {
        return new SimpleItemLoot(deferredItem.getId().toString(), chance, LootConfig.fromResourceLocationList(lootTables), deferredItem);
    }

    public void run(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot) {
        String tableIdString = lootTableId.toString();
        for (String string : lootTables.value) {
            if (Pattern.matches(string, tableIdString)) {
                for (int r = 0; r < rolls.value; r++) {
                    double i = Math.floor(chance.value);
                    for (int j = 0; j < i; j++) {
                        stackGetter.accept(generatedLoot);
                    }

                    double i2 = chance.value - i;
                    if (i2 >= Math.random()) {
                        stackGetter.accept(generatedLoot);
                    }
                }
                break;
            }
        }
    }

    public String getId() {
        return id;
    }

    public static class DefaultValueHolder<T> {
        public T value;
        public T defaultValue;

        public DefaultValueHolder(T value, T defaultValue) {
            this.value = value;
            this.defaultValue = defaultValue;
        }

        public DefaultValueHolder(T value) {
            this(value, value);
        }
    }

}
