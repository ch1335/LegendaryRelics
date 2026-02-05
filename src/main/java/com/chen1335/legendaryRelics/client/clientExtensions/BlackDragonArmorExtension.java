package com.chen1335.legendaryRelics.client.clientExtensions;

import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor.BlackDragonArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class BlackDragonArmorExtension implements IClientItemExtensions {

    @Override
    public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
        return EntityRendererRegister.BLACK_DRAGON_ARMOR_MODEL;
    }

    @Override
    public void setupModelAnimations(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        BlackDragonArmorModel<LivingEntity> blackDragon = (BlackDragonArmorModel<LivingEntity>) model;
        setPartVisibility(blackDragon, equipmentSlot);
        blackDragon.rightFoot.copyFrom(blackDragon.rightLeg);
        blackDragon.leftFoot.copyFrom(blackDragon.leftLeg);
        blackDragon.belt.copyFrom(blackDragon.body);
    }

    protected void setPartVisibility(BlackDragonArmorModel<LivingEntity> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD:
                model.head.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.belt.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightFoot.visible = true;
                model.leftFoot.visible = true;
        }
    }
}
