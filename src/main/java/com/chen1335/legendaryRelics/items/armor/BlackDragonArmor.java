package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.LRArmorHelper;
import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BlackDragonArmor extends ArmorItem implements LRArmorHelper {
    public static final ResourceLocation BlackDragonArmorTexture = LegendaryRelics.id("textures/armor/black_dragon.png");

    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public BlackDragonArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties.stacksTo(1));
        defaultModifiers = Suppliers.memoize(() -> {
            ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
            EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
            return this.getDefaultAttributeModifiers().withModifierAdded(
                    Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 5, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
            );
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());

    }


    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return BlackDragonArmorTexture;
    }


    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return super.getEnchantmentValue(stack);
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return defaultModifiers.get();
    }

    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return Component.empty().append(Component.translatable("item.legendary_relics.rarity.dark_gold")).append(Component.literal(" ")).append(super.getName(stack));
        }
        return super.getName(stack);
    }
}
