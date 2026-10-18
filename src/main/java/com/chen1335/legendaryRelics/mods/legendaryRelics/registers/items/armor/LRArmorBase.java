package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor;

import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.API.LRArmorHelper;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LRArmorBase extends ArmorItem implements LRArmorHelper, ILRItemExtension {
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public LRArmorBase(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        defaultModifiers = Suppliers.memoize(this::buildAttributes);
    }

    public static void handleAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, LivingEntity target, DamageSource source, float damage) {
        CalculatorArg args = CalculatorArg.simpleArg(target);
        for (ItemStack armorSlot : target.getArmorSlots()) {
            if (armorSlot.getItem() instanceof LRArmorBase lrArmorBase) {
                CalculatorArg arg = args.copy();
                CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, armorSlot);
                lrArmorBase.handleDamageReduce(event, arg, armorSlot,target,source);

            }
        }
    }

    protected void handleDamageReduce(LivingGetAttributeAdjustmentEvent event, CalculatorArg arg, ItemStack armorSlot, LivingEntity target, DamageSource source) {

    }


    protected ItemAttributeModifiers buildAttributes() {
        return super.getDefaultAttributeModifiers();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return defaultModifiers.get();
    }

    protected void getAttributeAdjustmentWhileLivingHurt(LivingEntity target, DamageSource source, float damage){

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return Component.empty().append(Component.translatable("item.legendary_relics.rarity.dark_gold")).append(Component.literal(" ")).append(super.getName(stack));
        }
        return super.getName(stack);
    }
}
