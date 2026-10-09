package com.chen1335.legendaryRelics.common.lootInjecter.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

/**
 * 池选择器：把池和它的下标传进来就地判断。
 *
 * <p>带 index 是因为"按位置选"和"按内容选"都要用；两者在 JSON 里可以叠加。
 * 读取池内内容依赖 AT 打开的 {@code LootPool.entries}。
 */
public sealed interface PoolCondition {

    Codec<PoolCondition> CODEC = Codec.STRING.dispatch("type", PoolCondition::type, PoolCondition::codecFor);

    String type();

    boolean test(int index, LootPool pool);

    private static MapCodec<? extends PoolCondition> codecFor(String type) {
        return switch (type) {
            case Any.TYPE -> Any.CODEC;
            case ByIndex.TYPE -> ByIndex.CODEC;
            case ByName.TYPE -> ByName.CODEC;
            case HasEntry.TYPE -> HasEntry.CODEC;
            case Not.TYPE -> Not.CODEC;
            case AnyOf.TYPE -> AnyOf.CODEC;
            case AllOf.TYPE -> AllOf.CODEC;
            default -> throw new IllegalArgumentException("Unknown pool condition: " + type);
        };
    }

    /** 匹配所有池。 */
    record Any() implements PoolCondition {
        public static final String TYPE = "any";
        public static final MapCodec<Any> CODEC = MapCodec.unit(Any::new);

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return true;
        }
    }

    /** 按下标匹配，JSON 里从 0 开始。 */
    record ByIndex(int index) implements PoolCondition {
        public static final String TYPE = "index";
        public static final MapCodec<ByIndex> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("index").forGetter(ByIndex::index)).apply(instance, ByIndex::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return this.index == index;
        }
    }

    /** 按池名匹配；无名池永远不匹配。 */
    record ByName(String name) implements PoolCondition {
        public static final String TYPE = "name";
        public static final MapCodec<ByName> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(ByName::name)).apply(instance, ByName::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return this.name.equals(pool.getName());
        }
    }

    /** 池里任意一个条目满足条件即算命中。 */
    record HasEntry(EntryCondition entryCondition) implements PoolCondition {
        public static final String TYPE = "has_entry";
        public static final MapCodec<HasEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                EntryCondition.CODEC.fieldOf("entryCondition").forGetter(HasEntry::entryCondition))
                .apply(instance, HasEntry::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            for (int i = 0; i < pool.entries.size(); i++) {
                if (this.entryCondition.test(i, pool.entries.get(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    /** 取反。 */
    record Not(PoolCondition child) implements PoolCondition {
        public static final String TYPE = "not";
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PoolCondition.CODEC.fieldOf("condition").forGetter(Not::child)).apply(instance, Not::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return !this.child.test(index, pool);
        }
    }

    /** 任一满足。 */
    record AnyOf(List<PoolCondition> children) implements PoolCondition {
        public static final String TYPE = "any_of";
        public static final MapCodec<AnyOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PoolCondition.CODEC.listOf().fieldOf("conditions").forGetter(AnyOf::children))
                .apply(instance, AnyOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return this.children.stream().anyMatch(child -> child.test(index, pool));
        }
    }

    /** 全部满足。 */
    record AllOf(List<PoolCondition> children) implements PoolCondition {
        public static final String TYPE = "all_of";
        public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                PoolCondition.CODEC.listOf().fieldOf("conditions").forGetter(AllOf::children))
                .apply(instance, AllOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(int index, LootPool pool) {
            return this.children.stream().allMatch(child -> child.test(index, pool));
        }
    }
}
