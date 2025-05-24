package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SacredTalisman extends LRCuriosBase {
    public SacredTalisman() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(
                    Constant.of(4),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.8F),
                                    Constant.of(1F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    public static FinalCalculator MAX_HEALTH_PERCENTAGE = FinalCalculator.of(Constant.of(0.25F));

    public static FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5),
                    Constant.of(7.5F)
            )
    );

    public static FinalCalculator UNDEAD_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.2F)
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.1", UNDEAD_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill",
                    MAX_HEALTH_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill",
                    MAX_HEALTH_PERCENTAGE.toRawComponent(),
                    SHIELD_AMOUNT.toRawComponent(),
                    SHIELD_LAST_TIME.toRawComponent()
            ).withColor(0xaeaeae));
        }
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.desc").withColor(5592405));
        tooltipComponents.add(Component.translatable("legendary_relics.cooldown", 120).withColor(5592405));
    }

    @Override
    public void handleLivingDamageEventLowest(LivingDamageEvent.Post event, CalculatorArg calculatorArg, ItemStack itemStack) {
        if (event.getEntity() instanceof Player player) {
            if (!player.getCooldowns().isOnCooldown(LRItems.SACRED_TALISMAN.get())) {
                if (player.getHealth() <= player.getMaxHealth() * SacredTalisman.MAX_HEALTH_PERCENTAGE.getValue(calculatorArg)) {
                    ShieldAPI.addCommonTimeLimitedShield(player, SacredTalisman.SHIELD_AMOUNT.getValue(calculatorArg), (int) (SacredTalisman.SHIELD_LAST_TIME.getValue(calculatorArg) * 20));
                    if (player.isDeadOrDying()) {
                        player.setHealth(1);
                    }
                    player.getCooldowns().addCooldown(LRItems.SACRED_TALISMAN.get(), 120 * 20);
                }
            }
        }
    }

    @Override
    public void handleLivingIncomingDamageEventLowest(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker.getType().is(EntityTypeTags.UNDEAD)) {
                event.setAmount(event.getAmount() * (1 - SacredTalisman.UNDEAD_REDUCE.getValue(newArgs)));
            }
        }
    }
}
