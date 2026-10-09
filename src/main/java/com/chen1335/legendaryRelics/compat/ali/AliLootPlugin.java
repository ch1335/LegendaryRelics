package com.chen1335.legendaryRelics.compat.ali;

import com.chen1335.legendaryRelics.common.lootCondition.ChanceModifier;
import com.chen1335.legendaryRelics.common.lootCondition.ChanceModifierCondition;
import com.yanny.ali.api.AliEntrypoint;
import com.yanny.ali.api.IPlugin;
import com.yanny.ali.api.IServerRegistry;
import com.yanny.ali.api.IServerUtils;
import com.yanny.ali.api.ITooltipNode;
import com.yanny.ali.plugin.common.tooltip.BranchTooltipNode;
import com.yanny.ali.plugin.common.tooltip.ValueTooltipNode;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

/**
 * ALI 的战利品谓词显示兼容：把 {@code chance_modifiers} 显示成嵌套的加工树。
 *
 * <p>ALI 靠注解扫描发现插件：没装 ALI 时本类不会被加载，所以本类不能被自己的代码引用。
 *
 * <p>不注册时 ALI 会反射遍历字段，把每个 modifier 渲染成"未完成的实现"加类名，所以必须逐类型自己拼。
 * 配色交给 ALI 自己的节点：分支标题走 TEXT_STYLE（金），{@link ValueTooltipNode} 的键值对是金色标签配青色值。
 */
@AliEntrypoint
public class AliLootPlugin implements IPlugin {
    private static final String ROOT = "legendary_relics.ali.condition.chance_modifiers";
    private static final String WRAPPER = "legendary_relics.ali.wrapper";

    @Override
    public String getModId() {
        return "legendary_relics";
    }

    @Override
    public void registerServer(IServerRegistry registry) {
        registry.registerConditionTooltip(ChanceModifierCondition.class, AliLootPlugin::conditionTooltip);
    }

    private static ITooltipNode conditionTooltip(IServerUtils utils, ChanceModifierCondition condition) {
        BranchTooltipNode.Builder root = BranchTooltipNode.branch();

        for (ChanceModifier modifier : condition.modifiers()) {
            root.add(describe(modifier));
        }

        return root.build(ROOT);
    }

    /** sealed 接口在同一文件里，所以这里可以直接穷举，加新类型时编译器会提醒。 */
    private static ITooltipNode describe(ChanceModifier modifier) {
        return switch (modifier) {
            case ChanceModifier.SetChance it -> leaf("property.set_chance", number(it.value()));
            case ChanceModifier.Multiply it -> leaf("property.multiply_factor", number(it.factor()));
            case ChanceModifier.Add it -> leaf("property.add_value", number(it.value()));
            case ChanceModifier.Invert ignored -> leaf("property.invert", "1 - chance");
            case ChanceModifier.Clamp it -> BranchTooltipNode.branch()
                    .add(leaf("property.min", number(it.min())))
                    .add(leaf("property.max", number(it.max())))
                    .build(key("property.range"));
            case ChanceModifier.ToolEnchantmentLevel it -> BranchTooltipNode.branch()
                    .add(operation(it.operation(), leaf("property.per_level", number(it.perLevel()))))
                    .add(leafTranslation("property.enchantment", enchantmentName(it.enchantment())))
                    .build(key("property.enchantment_modifier"));
            case ChanceModifier.EntityAttribute it -> BranchTooltipNode.branch()
                    .add(operation(it.operation(), leaf("property.per_point", number(it.perPoint()))))
                    .add(leafTranslation("property.attribute", it.attribute().value().getDescriptionId()))
                    .build(key("property.attribute_modifier"));
            case ChanceModifier.DifficultyFactor it -> BranchTooltipNode.branch()
                    .add(leaf("property.peaceful", difficulty(it, it.peaceful())))
                    .add(leaf("property.easy", difficulty(it, it.easy())))
                    .add(leaf("property.normal", difficulty(it, it.normal())))
                    .add(leaf("property.hard", difficulty(it, it.hard())))
                    .build(key("property.difficulty_modifier"));
        };
    }

    /** 运算方式自己一层，把"每级/每点增加多少"挂在它下面。 */
    private static ITooltipNode operation(ChanceModifier.Operation operation, ITooltipNode value) {
        return BranchTooltipNode.branch()
                .add(value)
                .build(key("operation." + operation.getSerializedName()));
    }

    /** 「金色标签 : 青色值」，外层 WRAPPER 是只含一个 %s 的键。 */
    private static ITooltipNode leaf(String property, String value) {
        return ValueTooltipNode.keyValue(ValueTooltipNode.translate(key(property)), value).build(WRAPPER);
    }

    /**
     * 值为翻译键的版本：附魔与属性的名字本身就是本地化键，必须再过一次 {@code translate}。
     *
     * <p>{@code keyValue} 的值走 {@code transform}，只有带 {@code \ue000} 前缀才会被翻成
     * {@link net.minecraft.network.chat.Component#translatable}，否则会原样显示成 "enchantment.minecraft.looting"。
     */
    private static ITooltipNode leafTranslation(String property, String translationKey) {
        return ValueTooltipNode.keyValue(ValueTooltipNode.translate(key(property)),
                ValueTooltipNode.translate(translationKey)).build(WRAPPER);
    }

    /** 留空的难度档位按语义取中性值，和实际计算保持一致。 */
    private static String difficulty(ChanceModifier.DifficultyFactor modifier, Optional<Double> value) {
        return "x" + value.orElse(modifier.operation().neutral());
    }

    /**
     * 附魔是数据驱动注册表，名字按 {@code enchantment.<命名空间>.<路径>} 取。
     *
     * <p>属性不能用同样的拼法：原版 {@code generic.luck} 这种路径带点，拼出来会多一层命名空间。
     */
    private static String enchantmentName(ResourceKey<Enchantment> enchantment) {
        return "enchantment." + enchantment.location().getNamespace() + "." + enchantment.location().getPath();
    }

    private static String number(double value) {
        return String.valueOf(value);
    }

    private static String key(String path) {
        return "legendary_relics.ali." + path;
    }
}
