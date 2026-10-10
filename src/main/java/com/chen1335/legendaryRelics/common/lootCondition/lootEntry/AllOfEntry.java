package com.chen1335.legendaryRelics.common.lootCondition.lootEntry;

import com.chen1335.legendaryRelics.API.objects.LRLootPoolEntries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

/**
 * 被抽中一次，就把所有孩子都产出。
 *
 * <p>用来表达"同一个池里多个各自独立的概率条目"：原版的 group/sequence 只是把孩子的权重
 * 摊平进池的候选列表，addRandomItem 随后仍然按权重抽一个，所以"N 个条目"会变成"每次只出 1 个"。
 * 而 createItemStack 收到的是 Consumer，可以被调用任意多次，这里就借它把全部孩子都产出去。
 *
 * <p>条件挂在【孩子】上，池里只有这一个条目，所以孩子之间是各自独立判定，权重占比恒为 1。
 */
public class AllOfEntry extends LootPoolSingletonContainer {
    public static final MapCodec<AllOfEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootPoolEntries.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter(AllOfEntry::children)
    ).and(LootPoolSingletonContainer.singletonFields(instance)).apply(instance, AllOfEntry::new));

    private final List<LootPoolEntryContainer> children;

    private AllOfEntry(List<LootPoolEntryContainer> children, int weight, int quality,
                       List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.children = children;
    }

    public List<LootPoolEntryContainer> children() {
        return this.children;
    }

    @Override
    public LootPoolEntryType getType() {
        return LRLootPoolEntries.ALL_OF.get();
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootContext context) {
        for (LootPoolEntryContainer child : this.children) {
            child.expand(context, entry -> entry.createItemStack(consumer, context));
        }
    }

    public static Builder allOf(List<LootPoolEntryContainer.Builder<?>> children) {
        return new Builder(children.stream().map(LootPoolEntryContainer.Builder::build).toList());
    }

    /** 池里只有一个条目时权重没有意义，所以不给外部设置权重/品质的入口。 */
    public static class Builder extends LootPoolSingletonContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> children;

        private Builder(List<LootPoolEntryContainer> children) {
            this.children = children;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new AllOfEntry(this.children, this.weight, this.quality,
                    this.getConditions(), this.getFunctions());
        }
    }
}