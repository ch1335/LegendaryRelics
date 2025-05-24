package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.LRArmorHelper;
import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonChestPlate extends BlackDragonArmor {
    public BlackDragonChestPlate() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC));
    }

    public static FinalCalculator PHYSICAL_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    public static FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(
            Add.of(
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.5F),
                                    Constant.of(0.75F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.05F),
                                    Constant.of(0.1F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );


    public static FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.15F),
                            Constant.of(0.25F)
                    ),
                    EntityAttributeValue.of(Attributes.MAX_HEALTH)
            )
    );

    public static FinalCalculator COOL_DOWN = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(12F),
                    Constant.of(8F)
            )
    );

    public static FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5F),
                    Constant.of(6F)
            )
    );

    public static void handleAttack(LivingIncomingDamageEvent event, Player playerAttacker, CalculatorArg newArgs, ItemStack itemStack) {
        if (!playerAttacker.getCooldowns().isOnCooldown(itemStack.getItem())) {
            event.setAmount(event.getAmount() + DAMAGE_INCREASE.getValue(newArgs));
            ShieldAPI.addCommonDecayShield(playerAttacker, SHIELD_AMOUNT.getValue(newArgs), SHIELD_LAST_TIME.getInt(newArgs) * 20);
            playerAttacker.getCooldowns().addCooldown(itemStack.getItem(), COOL_DOWN.getInt(newArgs) * 20);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.1", PHYSICAL_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        Level level = context.level();
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.2",
                    DAMAGE_INCREASE.toComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                    COOL_DOWN.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.2",
                    DAMAGE_INCREASE.toRawComponent(),
                    SHIELD_LAST_TIME.toRawComponent(),
                    SHIELD_AMOUNT.toRawComponent(),
                    COOL_DOWN.toRawComponent()
            ).withColor(0xaeaeae));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {
        if (event.getSource().is(Tags.DamageTypes.IS_PHYSICAL)) {
            event.setAmount(event.getAmount() * (1 - PHYSICAL_DAMAGE_REDUCE.getValue(arg)));
        }
    }
}
