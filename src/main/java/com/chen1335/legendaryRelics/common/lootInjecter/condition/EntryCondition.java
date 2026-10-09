package com.chen1335.legendaryRelics.common.lootInjecter.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;

import java.util.List;

/**
 * 条目选择器：把条目和它在池内的下标传进来就地判断。
 *
 * <p>读条目内容全靠 AT：物品/标签/权重在单例基类上，条件挂在最底层的容器基类上。
 * 复合条目（alternatives/sequence/group）只看得到 children，不递归。
 */
public sealed interface EntryCondition {

    Codec<EntryCondition> CODEC = Codec.STRING.dispatch("type", EntryCondition::type, EntryCondition::codecFor);

    String type();

    boolean test(int index, LootPoolEntryContainer entry);

    private static MapCodec<? extends EntryCondition> codecFor(String type) {
        return switch (type) {
            case Any.TYPE -> Any.CODEC;
            case ByIndex.TYPE -> ByIndex.CODEC;
            case IsItem.TYPE -> IsItem.CODEC;
            case IsTag.TYPE -> IsTag.CODEC;
            case IsType.TYPE -> IsType.CODEC;
            case WeightAtLeast.TYPE -> WeightAtLeast.CODEC;
            case HasCondition.TYPE -> HasCondition.CODEC;
            case HasFunction.TYPE -> HasFunction.CODEC;
            case HasChild.TYPE -> HasChild.CODEC;
            case Not.TYPE -> Not.CODEC;
            case AnyOf.TYPE -> AnyOf.CODEC;
            case AllOf.TYPE -> AllOf.CODEC;
            default -> throw new IllegalArgumentException("Unknown entry condition: " + type);
        };
    }

    /** 匹配所有条目。 */
    record Any() implements EntryCondition {
        public static final String TYPE = "any";
        public static final MapCodec<Any> CODEC = MapCodec.unit(Any::new);

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return true;
        }
    }

    /** 按池内下标匹配。 */
    record ByIndex(int index) implements EntryCondition {
        public static final String TYPE = "index";
        public static final MapCodec<ByIndex> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("index").forGetter(ByIndex::index)).apply(instance, ByIndex::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return this.index == index;
        }
    }

    /** 匹配某个物品条目。 */
    record IsItem(ResourceLocation item) implements EntryCondition {
        public static final String TYPE = "item";
        public static final MapCodec<IsItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(IsItem::item)).apply(instance, IsItem::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            if (!(entry instanceof LootItem lootItem)) {
                return false;
            }

            return lootItem.item.unwrapKey().map(ResourceKey::location).map(this.item::equals).orElse(false);
        }
    }

    /** 匹配某个物品标签条目。 */
    record IsTag(TagKey<Item> tag) implements EntryCondition {
        public static final String TYPE = "tag";
        public static final MapCodec<IsTag> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(IsTag::tag)).apply(instance, IsTag::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return entry instanceof TagEntry tagEntry && tagEntry.tag.equals(this.tag);
        }
    }

    /** 按条目类型匹配，例如 {@code minecraft:item}、{@code minecraft:alternatives}。 */
    record IsType(ResourceLocation entryType) implements EntryCondition {
        public static final String TYPE = "entry_type";
        public static final MapCodec<IsType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("entryType").forGetter(IsType::entryType)).apply(instance, IsType::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return this.entryType.equals(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.getKey(entry.getType()));
        }
    }

    /** 权重下限；非单例条目（复合条目）没有权重，直接不匹配。 */
    record WeightAtLeast(int weight) implements EntryCondition {
        public static final String TYPE = "weight_at_least";
        public static final MapCodec<WeightAtLeast> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("weight").forGetter(WeightAtLeast::weight)).apply(instance, WeightAtLeast::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return entry instanceof LootPoolSingletonContainer singleton && singleton.weight >= this.weight;
        }
    }

    /** 条目身上是否已经挂着某种条件，按条件类型 id 比较。 */
    record HasCondition(ResourceLocation conditionType) implements EntryCondition {
        public static final String TYPE = "has_condition";
        public static final MapCodec<HasCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("conditionType").forGetter(HasCondition::conditionType))
                .apply(instance, HasCondition::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return entry.conditions.stream()
                    .anyMatch(condition -> this.conditionType.equals(
                            BuiltInRegistries.LOOT_CONDITION_TYPE.getKey(condition.getType())));
        }
    }

    /** 条目身上是否已经挂着某种函数，按函数类型 id 比较。 */
    record HasFunction(ResourceLocation functionType) implements EntryCondition {
        public static final String TYPE = "has_function";
        public static final MapCodec<HasFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("functionType").forGetter(HasFunction::functionType))
                .apply(instance, HasFunction::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            if (!(entry instanceof LootPoolSingletonContainer singleton)) {
                return false;
            }

            return singleton.functions.stream()
                    .anyMatch(function -> this.functionType.equals(
                            BuiltInRegistries.LOOT_FUNCTION_TYPE.getKey(function.getType())));
        }
    }

    /** 复合条目里任意子条目满足条件。 */
    record HasChild(EntryCondition child) implements EntryCondition {
        public static final String TYPE = "has_child";
        public static final MapCodec<HasChild> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EntryCondition.CODEC.fieldOf("entryCondition").forGetter(HasChild::child)).apply(instance, HasChild::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            if (!(entry instanceof CompositeEntryBase composite)) {
                return false;
            }

            for (int i = 0; i < composite.children.size(); i++) {
                if (this.child.test(i, composite.children.get(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    /** 取反。 */
    record Not(EntryCondition child) implements EntryCondition {
        public static final String TYPE = "not";
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EntryCondition.CODEC.fieldOf("condition").forGetter(Not::child)).apply(instance, Not::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return !this.child.test(index, entry);
        }
    }

    /** 任一满足。 */
    record AnyOf(List<EntryCondition> children) implements EntryCondition {
        public static final String TYPE = "any_of";
        public static final MapCodec<AnyOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EntryCondition.CODEC.listOf().fieldOf("conditions").forGetter(AnyOf::children))
                .apply(instance, AnyOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return this.children.stream().anyMatch(child -> child.test(index, entry));
        }
    }

    /** 全部满足。 */
    record AllOf(List<EntryCondition> children) implements EntryCondition {
        public static final String TYPE = "all_of";
        public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EntryCondition.CODEC.listOf().fieldOf("conditions").forGetter(AllOf::children))
                .apply(instance, AllOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPoolEntryContainer entry) {
            return this.children.stream().allMatch(child -> child.test(index, entry));
        }
    }
}
