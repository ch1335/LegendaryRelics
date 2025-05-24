package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRShieldType;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import com.chen1335.shieldSystem.shieldSystem.ShieldInstanceHolder;
import com.chen1335.shieldSystem.shieldSystem.UnitShield;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ShieldRegenerator extends LRCuriosBase {
    public ShieldRegenerator() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(3),
            Constant.of(2.5F)
    ));
    public static FinalCalculator AMOUNT_PER_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(1),
            Constant.of(1.2F)
    ));
    public static FinalCalculator MAX_SHIELD = FinalCalculator.of(Mul.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            ),
            EntityAttributeValue.of(Attributes.MAX_HEALTH)
    ));


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.shield_regenerator.skill.1",
                    COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args),
                    AMOUNT_PER_ADD.toComponent(tooltipFlag.hasShiftDown(), args),
                    MAX_SHIELD.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.shield_regenerator.skill.1",
                    COOLDOWN.toRawComponent(),
                    AMOUNT_PER_ADD.toRawComponent(),
                    MAX_SHIELD.toRawComponent()
            ).withColor(0xaeaeae));
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ENTITY.putArg(args, entity);
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        if (entity.level().getGameTime() % (COOLDOWN.getInt(args) * 20L) == 0) {
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null) {
                instance.getShield().addShieldAmount(AMOUNT_PER_ADD.getValue(args), MAX_SHIELD.getValue(args));
            }
        }
    }
}
