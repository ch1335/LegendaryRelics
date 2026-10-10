package com.chen1335.legendaryRelics.common.lootInjecter.condition;

import com.google.common.collect.Sets;
import com.chen1335.legendaryRelics.API.objects.LRLootInjectorOps;
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
public interface TableCondition {

    /** 按注册表名分发，别的模组注册进 LRLootInjectorOps.TABLE_CONDITION_KEY 就能加自己的选择器。 */
    Codec<TableCondition> CODEC = LRLootInjectorOps.TABLE_CONDITIONS.byNameCodec()
            .dispatch("type", TableCondition::getType, TableConditionType::codec);

    /** 类型注册表项，json 里写成 命名空间:路径。 */
    TableConditionType getType();

    boolean test(ResourceLocation id, LootTable table);

    /** 标签在数据包 reload 时会变，派生出来的表 id 集合必须跟着失效。 */
    static void clearCaches() {
        TagTableCache.clear();
    }

    /** 匹配所有表。 */
    record All() implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_ALL.get();
        }
        public static final MapCodec<All> CODEC = MapCodec.unit(All::new);
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return true;
        }
    }

    /** 按表 id 精确匹配。 */
    record ById(ResourceLocation id) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_BY_ID.get();
        }
        public static final MapCodec<ById> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ById::id)).apply(instance, ById::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.id.equals(id);
        }
    }

    /** 按命名空间匹配，例如全部 legendary_relics 的表。 */
    record Namespace(String namespace) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_NAMESPACE.get();
        }
        public static final MapCodec<Namespace> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("namespace").forGetter(Namespace::namespace)).apply(instance, Namespace::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.namespace.equals(id.getNamespace());
        }
    }

    /** 按路径前缀匹配，例如 {@code entities/} 就是所有实体表。 */
    record PathPrefix(String prefix) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_PATH_PREFIX.get();
        }
        public static final MapCodec<PathPrefix> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("prefix").forGetter(PathPrefix::prefix)).apply(instance, PathPrefix::new));
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
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_ENTITY_TAG.get();
        }
        public static final MapCodec<EntityTagTable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(EntityTagTable::tag))
                .apply(instance, EntityTagTable::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return TagTableCache.entityTables(this.tag).contains(id);
        }
    }

    /** 匹配"某个方块标签下所有方块的战利品表"，同样有多对一的共享问题。 */
    record BlockTagTable(TagKey<Block> tag) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_BLOCK_TAG.get();
        }
        public static final MapCodec<BlockTagTable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(BlockTagTable::tag))
                .apply(instance, BlockTagTable::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return TagTableCache.blockTables(this.tag).contains(id);
        }
    }

    /** 取反。 */
    record Not(TableCondition child) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_NOT.get();
        }
        public static final MapCodec<Not> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.fieldOf("condition").forGetter(Not::child)).apply(instance, Not::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return !this.child.test(id, table);
        }
    }

    /** 任一满足；JSON 里写成数组时等价于 {@link AllOf}。 */
    record AnyOf(List<TableCondition> children) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_ANY_OF.get();
        }
        public static final MapCodec<AnyOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.listOf().fieldOf("conditions").forGetter(AnyOf::children)).apply(instance, AnyOf::new));
        @Override
        public boolean test(ResourceLocation id, LootTable table) {
            return this.children.stream().anyMatch(child -> child.test(id, table));
        }
    }

    /** 全部满足。 */
    record AllOf(List<TableCondition> children) implements TableCondition {
        @Override
        public TableConditionType getType() {
            return LRLootInjectorOps.TABLE_ALL_OF.get();
        }
        public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TableCondition.CODEC.listOf().fieldOf("conditions").forGetter(AllOf::children)).apply(instance, AllOf::new));
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
