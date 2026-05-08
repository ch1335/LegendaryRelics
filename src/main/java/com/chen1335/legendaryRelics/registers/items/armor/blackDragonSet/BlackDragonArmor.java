package com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.registers.items.armor.LRArmorBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlackDragonArmor extends LRArmorBase {
    public static final ResourceLocation BlackDragonArmorTexture = LegendaryRelics.id("textures/armor/black_dragon.png");

    public BlackDragonArmor(Type type, Properties properties) {
        super(LRArmorMaterials.BLACK_DRAGON, type, properties.stacksTo(1));
    }


    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return BlackDragonArmorTexture;
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.black_dragon." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 5, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        );
    }

    @Override
    public int maxToolTipWith() {
        return 270;
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {

    }

}
