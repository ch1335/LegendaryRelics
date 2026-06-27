package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.registers.dataComponentTypes.CollectedMinerals;
import com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects.OreCollectorEffect;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.Optional;

@Mixin(CurioInventoryCapability.class)
public class CurioInventoryCapabilityMixin {
    @Shadow
    @Final
    LivingEntity livingEntity;

    @ModifyReturnValue(method = "getFortuneLevel", at = @At("RETURN"))
    private int ModifyFortuneLevel(int original) {
        if (LegendaryRelics.isApothicAttributesExtensionLoaded()) {
            return original;
        }
        Optional<InfoHolder<OreCollectorEffect>> pairOptional = EquipmentEffectAPI.findBestEffect(this.livingEntity, LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value());
        if (pairOptional.isPresent()) {
            InfoHolder<OreCollectorEffect> pair = pairOptional.get();
            CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.itemStack(), pair.effect());
            int addition = pair.itemStack().getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount() >= OreCollectorEffect.COLLECTED_MINERALS_REQUIRE.getInt(args) ? 1 : 0;
            return original + (int) (OreCollectorEffect.DEFAULT.getValue(args) + addition);
        } else {
            return original;
        }
    }
}
