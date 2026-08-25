package com.chen1335.legendaryRelics.registers.items.armor.twistedSet;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.registers.items.armor.LRArmorBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

public class TwistedArmor extends LRArmorBase {
    public static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/armor/twisted_armor.png");

    public TwistedArmor(Type type, Properties properties) {
        super(LRArmorMaterials.TWISTED, type, properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.twisted." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 2, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        ).withModifierAdded(
                AttributesGetter.arrowDamage(), new AttributeModifier(resourcelocation, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentslotgroup
        );
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return TEXTURE;
    }
}
