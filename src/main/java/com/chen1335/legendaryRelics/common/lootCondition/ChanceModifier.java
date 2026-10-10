package com.chen1335.legendaryRelics.common.lootCondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;
import java.util.function.Function;

/**
 * 概率加工器：把当前概率变成新概率，条件会按列表顺序依次应用。
 *
 * <p>概率从 1.0 起算，所以「乘 0.1」等同于原版 10% 的随机概率，多个加工器可以自由串联。
 * 加新种类只要补一个 record 和 {@link #CODEC} 里的分支，不需要额外的注册表。
 *
 * <p>每种类型用 MapCodec 而不是 Codec：Codec.dispatch 有多个重载，传方法引用会歧义。
 */
public sealed interface ChanceModifier {

    Codec<ChanceModifier> CODEC = Codec.STRING.dispatch("type", ChanceModifier::type, ChanceModifier::codecFor);

    /** JSON 里的类型名。 */
    String type();

    double apply(double chance, LootContext context);

    private static MapCodec<? extends ChanceModifier> codecFor(String type) {
        return switch (type) {
            case Add.TYPE -> Add.CODEC;
            case Multiply.TYPE -> Multiply.CODEC;
            case SetChance.TYPE -> SetChance.CODEC;
            case Clamp.TYPE -> Clamp.CODEC;
            case Invert.TYPE -> Invert.CODEC;
            case DifficultyFactor.TYPE -> DifficultyFactor.CODEC;
            case ToolEnchantmentLevel.TYPE -> ToolEnchantmentLevel.CODEC;
            case EntityAttribute.TYPE -> EntityAttribute.CODEC;
            default -> throw new IllegalArgumentException("Unknown chance modifier: " + type);
        };
    }

    /** 概率加上一个值（可以是负数，用来折减）。 */
    record Add(double value) implements ChanceModifier {
        public static final String TYPE = "add";
        public static final MapCodec<Add> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("value").forGetter(Add::value)).apply(instance, Add::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return chance + this.value;
        }
    }

    /** 概率乘一个系数。 */
    record Multiply(double factor) implements ChanceModifier {
        public static final String TYPE = "multiply";
        public static final MapCodec<Multiply> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("factor").forGetter(Multiply::factor)).apply(instance, Multiply::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return chance * this.factor;
        }
    }

    /**
     * 直接设置概率，通常放在链首用来定初始概率，后面再依次折减／加成。
     *
     * <p>放在后面会把前面的结果整个丢掉，所以只在确实要覆盖时才用。
     */
    record SetChance(double value) implements ChanceModifier {
        public static final String TYPE = "set_chance";
        public static final MapCodec<SetChance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("value").forGetter(SetChance::value)).apply(instance, SetChance::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return this.value;
        }
    }

    /** 把概率夹到 [min, max]，用来兜底避免加工出负数或超过 1 的值。 */
    record Clamp(double min, double max) implements ChanceModifier {
        public static final String TYPE = "clamp";
        public static final MapCodec<Clamp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("min", 0.0D).forGetter(Clamp::min),
                Codec.DOUBLE.optionalFieldOf("max", 1.0D).forGetter(Clamp::max)).apply(instance, Clamp::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return Math.min(this.max, Math.max(this.min, chance));
        }
    }

    /** 取补：1 - 概率。 */
    record Invert() implements ChanceModifier {
        public static final String TYPE = "invert";
        public static final MapCodec<Invert> CODEC = MapCodec.unit(Invert::new);

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return 1.0D - chance;
        }
    }

    /**
     * 按游戏难度加工概率，支持加法与乘法两种语义。
     *
     * <p>每个档位都可以留空，留空时按 {@link Operation} 取中性值（乘法 1.0 / 加法 0.0），
     * 也就是该难度下不改变概率，所以只填要改的档位即可。
     */
    record DifficultyFactor(Operation operation,
                            Optional<Double> peaceful,
                            Optional<Double> easy,
                            Optional<Double> normal,
                            Optional<Double> hard) implements ChanceModifier {
        public static final String TYPE = "difficulty_factor";
        public static final MapCodec<DifficultyFactor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Operation.CODEC.optionalFieldOf("operation", Operation.MULTIPLY).forGetter(DifficultyFactor::operation),
                Codec.DOUBLE.optionalFieldOf("peaceful").forGetter(DifficultyFactor::peaceful),
                Codec.DOUBLE.optionalFieldOf("easy").forGetter(DifficultyFactor::easy),
                Codec.DOUBLE.optionalFieldOf("normal").forGetter(DifficultyFactor::normal),
                Codec.DOUBLE.optionalFieldOf("hard").forGetter(DifficultyFactor::hard)
        ).apply(instance, DifficultyFactor::new));

        /** 乘法版本，datagen 用起来比手搓 Optional 干净。 */
        public static DifficultyFactor multiply(double peaceful, double easy, double normal, double hard) {
            return of(Operation.MULTIPLY, peaceful, easy, normal, hard);
        }

        /** 加法版本。 */
        public static DifficultyFactor add(double peaceful, double easy, double normal, double hard) {
            return of(Operation.ADD, peaceful, easy, normal, hard);
        }

        private static DifficultyFactor of(Operation operation, double peaceful, double easy, double normal, double hard) {
            return new DifficultyFactor(operation, Optional.of(peaceful), Optional.of(easy),
                    Optional.of(normal), Optional.of(hard));
        }

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            return this.operation.apply(chance, this.valueFor(context.getLevel().getDifficulty()).orElse(this.operation.neutral()));
        }

        private Optional<Double> valueFor(Difficulty difficulty) {
            return switch (difficulty) {
                case PEACEFUL -> this.peaceful;
                case EASY -> this.easy;
                case NORMAL -> this.normal;
                case HARD -> this.hard;
            };
        }
    }

    /**
     * 按工具上某个附魔的等级加工概率。
     *
     * <p>{@code per_level} 是「每级增减多少」，加法是 {@code chance + 等级 × per_level}，
     * 乘法是 {@code chance ×（1 + 等级 × per_level）}；等级为 0 或 per_level 为 0 时都不改变概率。
     *
     * <p>注意这里的 multiply 和 {@link DifficultyFactor} 不同：那边乘的是档位值本身，这边乘的是「1 + 增量」。
     */
    record ToolEnchantmentLevel(ResourceKey<Enchantment> enchantment, Operation operation, double perLevel) implements ChanceModifier {
        public static final String TYPE = "tool_enchantment_level";
        public static final MapCodec<ToolEnchantmentLevel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(ToolEnchantmentLevel::enchantment),
                Operation.CODEC.optionalFieldOf("operation", Operation.MULTIPLY).forGetter(ToolEnchantmentLevel::operation),
                Codec.DOUBLE.optionalFieldOf("per_level", 0.05D).forGetter(ToolEnchantmentLevel::perLevel)
        ).apply(instance, ToolEnchantmentLevel::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            int level = this.levelOf(context);

            return level <= 0 ? chance : this.operation.applyScaled(chance, level * this.perLevel);
        }

        /**
         * 取不到工具或对应附魔时按 0 级处理，也就是不改变概率。
         *
         * <p>方块类战利品表才有 TOOL；实体类没有（见 LivingEntity#dropFromLootTable），
         * 那时退回攻击者身上取等级 —— 和原版 LootItemRandomChanceWithEnchantedBonusCondition 一致。
         *
         * <p>附魔是数据驱动注册表，没法像属性那样直接存 Holder，只能存 ResourceKey 再取。
         */
        private int levelOf(LootContext context) {
            Holder<Enchantment> holder = context.getLevel().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(this.enchantment);

            ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
            if (tool != null && !tool.isEmpty()) {
                return tool.getEnchantmentLevel(holder);
            }

            Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);

            return attacker instanceof LivingEntity living ? EnchantmentHelper.getEnchantmentLevel(holder, living) : 0;
        }
    }

    /**
     * 按某个实体的一条属性加工概率。
     *
     * <p>{@code per_point} 是「每点属性增减多少」，语义和 {@link ToolEnchantmentLevel} 一致。
     * 拿不到实体、或该实体身上没有这条属性时【原样返回】而不是当成 0，所以不装相关模组也不会意外清零。
     */
    record EntityAttribute(Holder<Attribute> attribute, EntitySource source, Operation operation, double perPoint) implements ChanceModifier {
        public static final String TYPE = "entity_attribute";
        public static final MapCodec<EntityAttribute> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(EntityAttribute::attribute),
                EntitySource.CODEC.optionalFieldOf("entity", EntitySource.ATTACKING_ENTITY).forGetter(EntityAttribute::source),
                Operation.CODEC.optionalFieldOf("operation", Operation.MULTIPLY).forGetter(EntityAttribute::operation),
                Codec.DOUBLE.optionalFieldOf("per_point", 0.05D).forGetter(EntityAttribute::perPoint)
        ).apply(instance, EntityAttribute::new));

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public double apply(double chance, LootContext context) {
            AttributeInstance instance = this.instanceOf(context);

            return instance == null ? chance : this.operation.applyScaled(chance, instance.getValue() * this.perPoint);
        }

        private AttributeInstance instanceOf(LootContext context) {
            if (!(this.source.resolve(context) instanceof LivingEntity living)) {
                return null;
            }

            // getInstance 在该实体没有这条属性时返回 null —— 正好就是"有就改、没有就不改"
            return living.getAttributes().getInstance(this.attribute);
        }
    }

    /** 用哪个上下文实体取属性。 */
    enum EntitySource implements StringRepresentable {
        /** 上下文主角：实体掉落里是被击杀的生物，宝箱/刷怪笼/考古里是开箱的玩家。 */
        THIS_ENTITY("this_entity", context -> context.getParamOrNull(LootContextParams.THIS_ENTITY)),
        /** 攻击者，最常用的一个。 */
        ATTACKING_ENTITY("attacking_entity", context -> context.getParamOrNull(LootContextParams.ATTACKING_ENTITY)),
        /** 直接攻击者（弓箭射出的箭等）。 */
        DIRECT_ATTACKING_ENTITY("direct_attacking_entity", context -> context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY)),
        /** 最后造成伤害的玩家。 */
        LAST_DAMAGE_PLAYER("last_damage_player", context -> context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER));

        public static final Codec<EntitySource> CODEC = StringRepresentable.fromEnum(EntitySource::values);

        private final String name;
        private final Function<LootContext, Entity> resolver;

        EntitySource(String name, Function<LootContext, Entity> resolver) {
            this.name = name;
            this.resolver = resolver;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public Entity resolve(LootContext context) {
            return this.resolver.apply(context);
        }
    }

    /** 难度档位的加工语义。 */
    enum Operation implements StringRepresentable {
        /** 概率 + 档位值。 */
        ADD("add"),
        /** 概率 × 档位值。 */
        MULTIPLY("multiply");

        public static final Codec<Operation> CODEC = StringRepresentable.fromEnum(Operation::values);

        private final String name;

        Operation(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        /** 档位留空时用的值：加法 0、乘法 1，都不改变概率。 */
        public double neutral() {
            return this == ADD ? 0.0D : 1.0D;
        }

        /** 纯语义：加法是 +value，乘法是 ×value。 */
        public double apply(double chance, double value) {
            return this == ADD ? chance + value : chance * value;
        }

        /** 增量语义：加法是 +delta，乘法是 ×（1 + delta）；delta 为 0 时两者都不改变。 */
        public double applyScaled(double chance, double delta) {
            return this == ADD ? chance + delta : chance * (1.0D + delta);
        }
    }
}
