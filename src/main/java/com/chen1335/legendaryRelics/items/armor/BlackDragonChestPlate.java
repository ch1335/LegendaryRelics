package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.BlackDragonChestPlateEffect;
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
        super(LRArmorMaterials.BLACK_DRAGON, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC));
    }

    public static ResourceLocation getElytraTexture() {
        return WINGS_LOCATION;
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new BlackDragonChestPlateEffect(2));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate_elytra").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean canElytraFly(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return true;
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }
}
