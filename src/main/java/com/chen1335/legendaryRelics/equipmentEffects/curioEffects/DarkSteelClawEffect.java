package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;

public class DarkSteelClawEffect extends LRCurioEffectBase {
    public static final ResourceLocation ATTACK_MODIFIER_ID = LegendaryRelics.id("dark_steel_claw_effect");

    public DarkSteelClawEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public float oldArmor = 0;

    public DarkSteelClawEffect(int level) {
        this(LREquipmentEffectTypes.DARK_STEEL_CLAW_EFFECT.value(), level);
    }

    public static FinalCalculator DAMAGE_ADD = FinalCalculator.of(
            Add.of(
                    Constant.of(1),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    EquipmentEffectLevelArg.of(level -> 0.1F + level * 0.05F)
                            ),
                            EntityAttributeValue.of(Attributes.ARMOR)
                    )
            )

    );

    @Override
    public void curioTick(ItemStack itemStack, LivingEntity wearer) {
        float currentArmor = wearer.getArmorValue();
        if (oldArmor != currentArmor) {
            markItemChanged(itemStack);
            oldArmor = currentArmor;
        }
    }

    @Override
    public void modifyCurioAttribute(CurioAttributeModifierEvent event) {
        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_MODIFIER_ID, DAMAGE_ADD.getValue(CalculatorArg.simpleArg(event.getSlotContext().entity(), event.getItemStack(), this)), AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_steel_claw.skill", DAMAGE_ADD.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("legendary_relics.items.skill.cannot_be_tacked").withColor(5592405));
    }
}
