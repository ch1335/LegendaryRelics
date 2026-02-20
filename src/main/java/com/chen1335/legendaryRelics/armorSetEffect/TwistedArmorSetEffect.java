package com.chen1335.legendaryRelics.armorSetEffect;

import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.API.objects.LRSetsEffects;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import com.chen1335.legendaryRelics.effectInstances.TwistedEffectInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class TwistedArmorSetEffect extends SetEffect {
    @Calculator
    public static final FinalCalculator GAIN_STACK_COOLDOWN = FinalCalculator.of(
            TieredBonus.of(List.of(1.5F, 1.5F, 0.75F, 0.5F))
    );
    @Calculator
    public static final FinalCalculator DRAW_SPEED_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.025F, 0.025F, 0.05F, 0.05F))
    );
    @Calculator
    public static final FinalCalculator ARROW_DAMAGE_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.03F, 0.03F, 0.04F, 0.05F))
    );
    @Calculator
    public static final FinalCalculator KEEP_TIME = FinalCalculator.of(
            TieredBonus.of(List.of(4F, 4F, 7F, 10F))
    );
    @Calculator
    public static final FinalCalculator ADDITION_ARROW_BASE_DAMAGE = FinalCalculator.of(
            TieredBonus.of(List.of(0.3F, 0.3F, 0.4F, 0.5F))
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {
        CalculatorArg args = buildArg(player);
        boolean shiftDown = tooltipFlag.hasShiftDown();
        list.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus", Component.translatable("set_effect.legendary_relics.twisted.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));
        list.add(Component.translatable("set_effect.legendary_relics.twisted.desc.1",
                GAIN_STACK_COOLDOWN.toComponent(shiftDown, args, ChatFormatting.YELLOW.getColor()),
                phaseShootingComponent()
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.twisted.desc.2",
                KEEP_TIME.toComponent(shiftDown, args),
                phaseShootingComponent(),
                phaseShootingComponent()
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.twisted.desc.3",
                DRAW_SPEED_PER_STACK.toPercentageComponent(shiftDown, args, ChatFormatting.BLUE.getColor()),
                ARROW_DAMAGE_PER_STACK.toPercentageComponent(shiftDown, args, ChatFormatting.BLUE.getColor())
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.twisted.desc.4",
                ADDITION_ARROW_BASE_DAMAGE.toPercentageComponent(shiftDown, args, ChatFormatting.RED.getColor())
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.armor_pieces_required", "2+"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static TwistedEffectInstance getEffectInstance(LivingEntity living) {
        return EntitySetsEffectData.getSetInstance(living, LRSetsEffects.TWISTED_ARMOR.value(), TwistedEffectInstance.class);
    }

    public static Component phaseShootingComponent() {
        return Component.empty().append("").append(Component.translatable("legendary_relics.stack.phase_shooting").withStyle(ChatFormatting.DARK_AQUA));
    }


    @Override
    public EffectInstance createInstance(int piece) {
        return new TwistedEffectInstance(piece);
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getDirectEntity() instanceof AbstractArrow abstractArrow && abstractArrow.getOwner() instanceof LivingEntity attacker) {
            TwistedEffectInstance instance = getEffectInstance(attacker);
            if (instance != null) {
                CalculatorArg args = instance.buildArgs(attacker);
                if (instance.coolDown <= 0) {
                    instance.addStack(1);
                    float cooldownValueSecond = GAIN_STACK_COOLDOWN.getValue(args);
                    instance.coolDown = (int) (cooldownValueSecond * 20);
                    instance.keepTime = (int) (KEEP_TIME.getValue(args) * 20);
                }
            }
        }
    }


    public static void LivingEntityUseItemEvent$Stop(LivingEntityUseItemEvent.Stop event) {
        if (event.getItem().getItem() instanceof ProjectileWeaponItem) {
            LivingEntity entity = event.getEntity();
            TwistedEffectInstance effectInstance = getEffectInstance(entity);
            if (effectInstance != null && effectInstance.stack >= 10) {
                float yRotOld = entity.getYRot();
                boolean damageableItem = event.getItem().isDamageableItem();
                int damageValue = event.getItem().getDamageValue();
                effectInstance.isDoingAdditionShoot = true;
                entity.setYRot(yRotOld + 12);
                event.getItem().releaseUsing(entity.level(), entity, event.getDuration());
                entity.setYRot(yRotOld - 12);
                event.getItem().releaseUsing(entity.level(), entity, event.getDuration());
                entity.setYRot(yRotOld);
                effectInstance.isDoingAdditionShoot = false;
                if (damageableItem) {
                    event.getItem().setDamageValue(damageValue);
                }
            }
        }
    }

    public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            if (entity instanceof AbstractArrow arrow && arrow.getOwner() instanceof LivingEntity owner) {
                TwistedEffectInstance effectInstance = TwistedArmorSetEffect.getEffectInstance(owner);
                if (effectInstance != null) {
                    effectInstance.modifyArrow(arrow,owner);
                }
            }
        }
    }
}
