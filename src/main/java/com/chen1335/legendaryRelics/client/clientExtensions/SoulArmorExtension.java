package com.chen1335.legendaryRelics.client.clientExtensions;

import com.chen1335.equipmentEffectLib.utils.Cast;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.module.armor.soulArmor.SoulArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class SoulArmorExtension implements IClientItemExtensions {
    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        return EntityRendererRegister.SOUL_ARMOR_MODEL;
    }

    @Override
    public void setupModelAnimations(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        SoulArmorModel<?> model1 = Cast.cast(model);
        model1.setAllVisible(false);
        if (equipmentSlot == EquipmentSlot.HEAD) {
            model1.head.visible = true;
        } else if (equipmentSlot == EquipmentSlot.CHEST) {
            model1.body.visible = true;
            model1.rightArm.visible = true;
            model1.leftArm.visible = true;
        } else if (equipmentSlot == EquipmentSlot.LEGS) {
            model1.belt.copyFrom(model1.body);
            model1.rightLeg.visible = true;
            model1.leftLeg.visible = true;
            model1.belt.visible = true;
        } else if (equipmentSlot == EquipmentSlot.FEET) {
            model1.rightFoot.copyFrom(model1.rightLeg);
            model1.leftFoot.copyFrom(model1.leftLeg);
            model1.rightFoot.visible = true;
            model1.leftFoot.visible = true;
        }
    }
}
