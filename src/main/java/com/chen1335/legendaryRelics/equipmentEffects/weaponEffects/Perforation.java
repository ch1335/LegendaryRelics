package com.chen1335.legendaryRelics.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.equipmentEffects.LRBaseEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;

public class Perforation extends LRBaseEffect {
    public static FinalCalculator PIERCE_LEVEL = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(2),
                    Constant.of(3)

            )
    );

    public Perforation(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.perforation", PIERCE_LEVEL.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
    }


    public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof LivingEntity living) {
            LREquipmentEffectTypes.PERFORATION.value().findBestEffect(living).ifPresent(infoHolder -> {
                CalculatorArg arg = CalculatorArg.simpleArg(living, infoHolder.itemStack(), infoHolder.effect());
                if (!arrow.getTags().contains("perforation_modified")) {
                    LRProjectileData data = arrow.getData(LRAttachmentTypes.PROJECTILE_DATA.get());
                    data.pierceLevel = data.pierceLevel + (int) PIERCE_LEVEL.getValue(arg);
                    arrow.addTag("perforation_modified");
                }
            });
        }
    }

}
