package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

/**
 * 寒域：位于寒冷群系时，大部分（正面）属性按比例提升。
 *
 * <p>与"在下界时提升"那条不同 —— 群系会随走动改变，而装备状态本身不变，
 * 所以除了 {@link #onActive}/{@link #onDeActive}，还要周期性检查群系变化。
 * 判定沿用原版自己的 {@code Biome#coldEnoughToSnow}（含温度修正与高度影响）。
 */
public class ColdDomainEffect extends LRCurioEffect {

    private static final ResourceLocation MODIFIER_ID = LegendaryRelics.id("cold_domain");

    /** 寒冷群系下的属性提升比例。 */
    @Calculator
    public static final FinalCalculator ATTRIBUTE_BOOST = FinalCalculator.of(Constant.of(0.05F));

    /** 周期性检查间隔（tick）。 */
    private static final int CHECK_INTERVAL = 20;

    public ColdDomainEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        Component boost = ComponentUtil.Attributes.attribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER,
                ATTRIBUTE_BOOST.toComponent(args, Style.EMPTY, 100, "%s%%"),
                "+");
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.cold_domain", boost)
                .withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    private static void addAttribute(ISlotContext slotContext, LivingEntity living, ItemStack itemStack, ColdDomainEffect effect) {
        CalculatorArg arg = CalculatorArg.simpleArg(living, itemStack, effect);
        AttributeInstance attribute = living.getAttribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER);
        if (attribute != null) {
            attribute.addTransientModifier(new AttributeModifier(slotContext.pathRL(MODIFIER_ID),ATTRIBUTE_BOOST.getValue(arg), AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void removeAttribute(ISlotContext slotContext, LivingEntity living) {
        AttributeInstance attribute = living.getAttribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER);
        if (attribute != null) {
            attribute.removeModifier(slotContext.pathRL(MODIFIER_ID));
        }
    }

    private static boolean isInColdBiome(LivingEntity living) {
        BlockPos pos = living.blockPosition();
        return living.level().getBiome(pos).is(Tags.Biomes.IS_COLD);
    }

    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        if (isInColdBiome(entity)) {
            addAttribute(slotContext, entity, itemStack, this);
        }
    }

    @Override
    public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        // 无条件移除，避免走出寒冷群系后还留着加成
        removeAttribute(slotContext, entity);
    }

    /** 周期性检查群系变化：装备状态不会随走动改变，所以必须自己查。 */
    public static void EntityTickEvent(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide) {
            return;
        }
        if (living.tickCount % CHECK_INTERVAL != 0) {
            return;
        }
        EquipmentEffectAPI.findBestEffect(living, LREquipmentEffectTypes.COLD_DOMAIN.value()).ifPresent(pair -> {
            if (isInColdBiome(living)) {
                addAttribute(pair.context(), living, pair.infoHolder().itemStack(), pair.infoHolder().effect());
            } else {
                removeAttribute(pair.context(), living);
            }
        });
    }
}
