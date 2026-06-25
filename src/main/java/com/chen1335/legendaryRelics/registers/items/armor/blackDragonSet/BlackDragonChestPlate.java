package com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonChestPlate extends BlackDragonArmor {
    private static final ResourceLocation WINGS_LOCATION = LegendaryRelics.id("textures/armor/black_dragon_chestplate_elytra.png");

    public BlackDragonChestPlate() {
        super(Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC));
    }

    public static ResourceLocation getElytraTexture() {
        return WINGS_LOCATION;
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value().create(1, EquipmentType.ARMOR));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate_elytra").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canElytraFly(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return true;
    }

    public boolean elytraFlightTick(@NotNull ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }
}
