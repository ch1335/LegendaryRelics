package com.chen1335.legendaryRelics.common.lootInjecter.condition;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 战利品表选择器：把表和它的 id 传进来就地判断，和 LootContext 无关，所以能在数据包加载阶段用。
 *
 * <p>注入器按 {@code (表数 × 注入器数)} 逐个测试，所以叶子条件要尽量便宜。
 *
 * <p>实体/方块的标签要先反查成"表 id 集合"：战利品表不知道自己的主人，只能遍历注册表建立映射。
 */
public sealed interface TableCondition {

    Codec<TableCondition> CODEC = Codec.STRING.dispatch("type", TableCondition::type, TableCondition::codecFor);

    /** JSON 里的类型名。用短名，因为这一层完全是我们自己的数据包格式。 */
    String type();

    boolean test(ResourceLocation id, LootTable table);

    /** 标签在数据包 reload 时会变，派生出来的表 id 集合必须跟着失效。 */
    static void clearCaches() {
        TagTableCache.clear();
    }

    private static MapCodec<? extends TableCondition> codecFor(String type) {
        return switch (type) {
            case All.TYPE -> All.CODEC;
            case ById.TYPE -> ById.CODEC;
            case Namespace.TYPE -> Namespace.CODEC;
            case PathPrefix.TYPE -> PathPrefix.CODEC;
            case EntityTagTable.TYPE -> EntityTagTable.CODEC;
            case BlockTagTable.TYPE -> BlockTagTable.CODEC;
            case Not.TYPE -> Not.CODEC;
            case AnyOf.TYPE -> AnyOf.CODEC;
            case AllOf.TYPE -> AllOf.CODEC;
            default -> throw new IllegalArgumentException("Unknown table condition: " + type);
        };
    }

    /** 匹配所有表。 */
    record All() implements TableCondition {
        public static final String TYPE = "all";
        public static final MapCodec<All> CODEC = MapCodec.unit(All::new);

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return true;
        }
    }

    /** 按表 id 精确匹配。 */
    record ById(ResourceLocation id) implements TableCondition {
        public static final String TYPE = "id";
        public static final MapCodec<ById> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ById::id)).apply(instance, ById::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.id.equals(id);
        }
    }

    /** 按命名空间匹配，例如全部 legendary_relics 的表。 */
    record Namespace(String namespace) implements TableCondition {
        public static final String TYPE = "namespace";
        public static final MapCodec<Namespace> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("namespace").forGetter(Namespace::namespace)).apply(instance, Namespace::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.namespace.equals(id.getNamespace());
        }
    }

    /** 按路径前缀匹配，例如 {@code entities/} 就是所有实体表。 */
    record PathPrefix(String prefix) implements TableCondition {
        public static final String TYPE = "path_prefix";
        public static final MapCodec<PathPrefix> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("prefix").forGetter(PathPrefix::prefix)).apply(instance, PathPrefix::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return id.getPath().startsWith(this.prefix);
        }
    }

    /**
     * 匹配"某个实体标签下所有实体的默认战利品表"。
     *
     * <p>注意战利品表是多对一的：不在标签里的实体可能共用同一张表，那种情况下改动会一起生效。
     */
    record EntityTagTable(TagKey<EntityType<?>> tag) implements TableCondition {
        public static final String TYPE = "entity_tag";
        public static final MapCodec<EntityTagTable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(EntityTagTable::tag))
                .apply(instance, EntityTagTable::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return TagTableCache.entityTables(this.tag).contains(id);
        }
    }

    /** 匹配"某个方块标签下所有方块的战利品表"，同样有多对一的共享问题。 */
    record BlockTagTable(TagKey<Block> tag) implements TableCondition {
        public static final String TYPE = "block_tag";
        public static final MapCodec<BlockTagTable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(BlockTagTable::tag))
                .apply(instance, BlockTagTable::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return TagTableCache.blockTables(this.tag).contains(id);
        }
    }

    /** 取反。 */
    record Not(TableCondition child) implements TableCondition {
        public static final String TYPE = "not";
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.fieldOf("condition").forGetter(Not::child)).apply(instance, Not::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return !this.child.test(id, table);
        }
    }

    /** 任一满足；JSON 里写成数组时等价于 {@link AllOf}。 */
    record AnyOf(List<TableCondition> children) implements TableCondition {
        public static final String TYPE = "any_of";
        public static final MapCodec<AnyOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.listOf().fieldOf("conditions").forGetter(AnyOf::children)).apply(instance, AnyOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.children.stream().anyMatch(child -> child.test(id, table));
        }
    }

    /** 全部满足。 */
    record AllOf(List<TableCondition> children) implements TableCondition {
        public static final String TYPE = "all_of";
        public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.listOf().fieldOf("conditions").forGetter(AllOf::children)).apply(instance, AllOf::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.children.stream().allMatch(child -> child.test(id, table));
        }
    }
}

/**
 * 标签 → 表 id 集合的缓存。
 *
 * <p>反查要遍历整个注册表，很贵，所以缓存起来；数据包 reload 时必须清空，否则标签改了不生效。
 */
final class TagTableCache {
    private static final Map<TagKey<?>, Set<ResourceLocation>> CACHE = new ConcurrentHashMap<>();

    private TagTableCache() {
    }

    static void clear() {
        CACHE.clear();
    }

    static Set<ResourceLocation> entityTables(TagKey<EntityType<?>> tag) {
        return CACHE.computeIfAbsent(tag, key -> BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> type.builtInRegistryHolder().is(tag))
                .map(EntityType::getDefaultLootTable)
                .map(ResourceKey::location)
                .collect(Sets::newHashSet, Set::add, Set::addAll));
    }

    static Set<ResourceLocation> blockTables(TagKey<Block> tag) {
        return CACHE.computeIfAbsent(tag, key -> BuiltInRegistries.BLOCK.stream()
                .filter(block -> block.defaultBlockState().is(tag))
                .map(Block::getLootTable)
                .filter(table -> table != null)
                .map(ResourceKey::location)
                .collect(Sets::newHashSet, Set::add, Set::addAll));
    }
}
