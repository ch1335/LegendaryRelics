package com.chen1335.legendaryRelics.client.clientExtensions;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.module.armor.infernoArmor.InfernoArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import com.chen1335.equipmentEffectLib.utils.Cast;

public class InfernoArmorExtension implements IClientItemExtensions {
    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if (itemStack.is(LRItems.INFERNO_BOOTS) || itemStack.is(LRItems.INFERNO_LEGGINGS)) {
            return original;
        }
        return EntityRendererRegister.INFERNO_ARMOR_MODEL;
    }

    @Override
    public void setupModelAnimations(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (equipmentSlot == EquipmentSlot.HEAD) {
            HumanoidModel<?> model1 = Cast.cast(model);
            model1.hat.visible = false;
        }
    }
}
