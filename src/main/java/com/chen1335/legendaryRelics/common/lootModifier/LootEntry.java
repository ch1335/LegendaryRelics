package com.chen1335.legendaryRelics.common.lootModifier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootEntry {
    private final String id;
    public DefaultValueHolder<Double> chance;
    public DefaultValueHolder<List<ResourceLocation>> lootTables;
    private final Consumer<ObjectArrayList<ItemStack>> consumer;

    public LootEntry(String id, double chance, List<ResourceLocation> lootTables, Consumer<ObjectArrayList<ItemStack>> consumer) {
        this.id = id;
        this.chance = new DefaultValueHolder<>(Double.parseDouble(String.format("%.2f", chance)));
        this.lootTables = new DefaultValueHolder<>(lootTables, List.copyOf(lootTables));
        this.consumer = consumer;
        LootModifier.LOOT_ENTRIES.put(id, this);
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();

        for (ResourceLocation resourceLocation : lootTables.value) {
            listTag.add(StringTag.valueOf(resourceLocation.toString()));
        }
        compoundTag.putDouble("chance", chance.value);
        compoundTag.put("lootTables", listTag);
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        chance.value = compoundTag.getDouble("chance");
        ListTag listTag = compoundTag.getList("lootTables", Tag.TAG_STRING);
        List<ResourceLocation> resourceLocations = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            resourceLocations.add(ResourceLocation.parse(listTag.getString(i)));
        }
        lootTables.value = resourceLocations;
    }

    public Component getComponent() {
        return Component.translatable("legendary_relics.loot_config." + id);
    }

    public static LootEntry of(String id, double chance, List<ResourceLocation> lootTables, Consumer<ObjectArrayList<ItemStack>> consumer) {
        return new LootEntry(id, chance, lootTables, consumer);
    }

    public static LootEntry ofSpecialLoot(String id, double chance, List<ResourceLocation> lootTables, Supplier<? extends Item> itemSupplier) {
        return of(id, chance, lootTables, itemStacks -> itemStacks.add(itemSupplier.get().getDefaultInstance()));
    }


    public static class SimpleItemLoot extends LootEntry {
        public Supplier<? extends Item> itemSupplier;

        public SimpleItemLoot(String id, double chance, List<ResourceLocation> lootTables, Supplier<? extends Item> itemSupplier) {
            super(id, chance, lootTables, itemStacks -> itemStacks.add(itemSupplier.get().getDefaultInstance()));
            this.itemSupplier = itemSupplier;
        }

        @Override
        public Component getComponent() {
            return itemSupplier.get().getDescription();
        }
    }

    public static LootEntry ofSimpleItem(String id, double chance, List<ResourceLocation> lootTables, Supplier<? extends Item> itemSupplier) {
        return new SimpleItemLoot(id, chance, lootTables, itemSupplier);
    }

    public static LootEntry ofSimpleItem(double chance, List<ResourceLocation> lootTables, DeferredItem<? extends Item> deferredItem) {
        return new SimpleItemLoot(deferredItem.getId().toString(), chance, lootTables, deferredItem);
    }

    public void run(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot) {
        if (lootTables.value.contains(lootTableId)) {
            double i = Math.floor(chance.value);
            for (int j = 0; j < i; j++) {
                consumer.accept(generatedLoot);
            }

            double i2 = chance.value - i;
            if (i2 >= Math.random()) {
                consumer.accept(generatedLoot);
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
            this.value = value;
            this.defaultValue = value;
        }
    }
}
