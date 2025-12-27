package com.chen1335.legendaryRelics.armorSetEffect;

import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.SingleCustomArg;
import com.chen1335.legendaryRelics.items.armor.BlackDragonArmor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class BlackDragonArmorSetEffect extends SetsEffectBase {
    public static final ResourceLocation BLACK_DRAGON_ATTRIBUTE_MULTIPLIER = LegendaryRelics.id("black_dragon_attribute_multiplier");
    public static SingleCustomArg<LivingEntity> BLACK_ARMOR_COUNT_GETTER = SingleCustomArg.register(LegendaryRelics.id("black_armor_count_getter"),CalculatorArg.ArgType.THIS_ENTITY, livingEntity -> {
        if (livingEntity == null) {
            return 0f;
        }
        int i = 0;
        for (ItemStack armorSlot : livingEntity.getArmorSlots()) {
            if (armorSlot.getItem() instanceof BlackDragonArmor) {
                i++;
            }
        }
        return (float) i;
    }, (livingEntity) -> Component.translatable("item.legendary_relics.arg_name.already_equipped_black_dragon_armor").withColor(16733695));


    public static FinalCalculator ATTRIBUTE_MULTIPLIER = FinalCalculator.of(
            Add.of(
                    Constant.of(0.02F, 3),
                    Mul.of(
                            Constant.of(0.02F, 3),
                            BLACK_ARMOR_COUNT_GETTER
                    )
            )
    );

    public static FinalCalculator HEALTH_REGAIN = FinalCalculator.of(
            Add.of(
                    Constant.of(1),
                    Mul.of(
                            Constant.of(0.01F),
                            Mul.of(
                                    EntityAttributeValue.of(Attributes.MAX_HEALTH),
                                    BLACK_ARMOR_COUNT_GETTER
                            )
                    )
            )
    );

    @SubscribeEvent
    public static void EntityTickPre(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity living && !living.level().isClientSide) {
            CalculatorArg args = CalculatorArg.simpleArg(living);
            if (!living.level().isClientSide && living.level().getGameTime() % 10 == 0 && living.getHealth() < living.getMaxHealth()) {
                float healthRegain = BlackDragonArmorSetEffect.HEALTH_REGAIN.getValue(args);
                if (BlackDragonArmorSetEffect.BLACK_ARMOR_COUNT_GETTER.getValue(args) == 0) {
                    healthRegain = 0;
                }
                living.heal(healthRegain / 10);
            }
        }
    }

    @SubscribeEvent
    public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        CalculatorArg args = CalculatorArg.simpleArg(livingEntity);
        if (!(event.getFrom().getItem() == event.getTo().getItem()) && (event.getFrom().getItem() instanceof BlackDragonArmor || event.getTo().getItem() instanceof BlackDragonArmor)) {
            float multiplier = BlackDragonArmorSetEffect.ATTRIBUTE_MULTIPLIER.getValue(args);
            if (BlackDragonArmorSetEffect.BLACK_ARMOR_COUNT_GETTER.getValue(args) == 0) {
                multiplier = 0;
            }
            for (AttributeInstance value : livingEntity.getAttributes().supplier.instances.values()) {
                AttributeInstance instance = livingEntity.getAttribute(value.getAttribute());
                if (instance != null && instance.getAttribute().value().sentiment == Attribute.Sentiment.POSITIVE) {
                    instance.removeModifier(BlackDragonArmorSetEffect.BLACK_DRAGON_ATTRIBUTE_MULTIPLIER);
                    if (multiplier > 0) {
                        instance.addPermanentModifier(new AttributeModifier(BlackDragonArmorSetEffect.BLACK_DRAGON_ATTRIBUTE_MULTIPLIER, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    }
                }
            }
        }
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ENTITY.putArg(args, player);
        tooltipComponents.add(Component.translatable("item.legendary_relics.set_effect.desc.1", Component.translatable("item.legendary_relics.black_dragon_set_effect.name").append("(" + LRClient.ENTITY_SETS_EFFECT_DATA.getOrDefault(this, 0) + "/4)").withColor(16733695)).withColor(16755200));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.1",
                ATTRIBUTE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(16733695));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.2",
                HEALTH_REGAIN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(16733695));
    }

}
