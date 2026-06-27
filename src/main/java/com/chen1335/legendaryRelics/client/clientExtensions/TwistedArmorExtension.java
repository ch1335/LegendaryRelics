package com.chen1335.legendaryRelics.client.clientExtensions;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.client.EntityRendererRegister;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import com.chen1335.equipmentEffectLib.utils.Cast;

public class TwistedArmorExtension implements IClientItemExtensions {
    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if (itemStack.is(LRItems.TWISTED_BOOTS) || itemStack.is(LRItems.TWISTED_LEGGINGS)) {
            return original;
        }
        return EntityRendererRegister.TWISTED_ARMOR_MODEL;
    }

    @Override
    public void setupModelAnimations(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (equipmentSlot == EquipmentSlot.HEAD) {
            HumanoidModel<?> model1 = Cast.cast(model);
            model1.hat.visible = false;
        }
    }
}
