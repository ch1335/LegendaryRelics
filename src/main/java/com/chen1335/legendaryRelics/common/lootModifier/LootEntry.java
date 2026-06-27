package com.chen1335.legendaryRelics.common.lootModifier;

import com.chen1335.legendaryRelics.config.LootConfig;
import com.chen1335.legendaryRelics.utils.valueHolder.DefaultValueHolder;
import com.chen1335.legendaryRelics.utils.valueHolder.ValueHolderWrapper;
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
    public final ValueHolderWrapper<Double> chance = new ValueHolderWrapper<>(new DefaultValueHolder<>(0D));
    public final ValueHolderWrapper<Integer> rolls = new ValueHolderWrapper<>(new DefaultValueHolder<>(1));
    public final ValueHolderWrapper<List<String>> lootTables = new ValueHolderWrapper<>(new DefaultValueHolder<>(List.of()));
    public final Consumer<ObjectArrayList<ItemStack>> stackGetter;
    public boolean isCreatedByRemote = false;
    public static final StreamCodec<ByteBuf, LootEntry> STREAM_CODEC = StreamCodec.of((byteBuf, lootEntry) -> {
        ByteBufCodecs.STRING_UTF8.encode(byteBuf, lootEntry.id);
        byteBuf.writeDouble(lootEntry.chance.get());
        byteBuf.writeInt(lootEntry.rolls.get());
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(byteBuf, lootEntry.lootTables.get());
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
        lootEntry.chance.set(chance);
        lootEntry.rolls.set(rolls);
        lootEntry.lootTables.set(lootTables);
        return lootEntry;
    });

    private LootEntry markCreatedByRemote() {
        isCreatedByRemote = true;
        return this;
    }

    public LootEntry(String id, double chance, List<String> lootTables, Consumer<ObjectArrayList<ItemStack>> consumer) {
        this.id = id;
        this.chance.setInnerHolder(new DefaultValueHolder<>(Double.parseDouble(String.format("%.2f", chance))));
        this.lootTables.setInnerHolder(new DefaultValueHolder<>(lootTables, List.copyOf(lootTables)));
        this.stackGetter = consumer;
        LootModifier.LOOT_ENTRIES.put(id, this);
    }

    public LootEntry rolls(int rolls) {
        this.rolls.setInnerHolder(new DefaultValueHolder<>(rolls));
        return this;
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();

        for (String resourceLocation : lootTables.get()) {
            listTag.add(StringTag.valueOf(resourceLocation));
        }
        compoundTag.putDouble("chance", chance.get());
        compoundTag.putDouble("rolls", rolls.get());
        compoundTag.put("lootTables", listTag);
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        chance.set(compoundTag.getDouble("chance"));
        rolls.set(compoundTag.getInt("rolls"));
        ListTag listTag = compoundTag.getList("lootTables", Tag.TAG_STRING);
        List<String> resourceLocations = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            resourceLocations.add(listTag.getString(i));
        }
        lootTables.set(resourceLocations);
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
        for (String string : lootTables.get()) {
            if (Pattern.matches(string, tableIdString)) {
                for (int r = 0; r < rolls.get(); r++) {
                    double i = Math.floor(chance.get());
                    for (int j = 0; j < i; j++) {
                        stackGetter.accept(generatedLoot);
                    }

                    double i2 = chance.get() - i;
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


}
