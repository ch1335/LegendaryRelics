package com.chen1335.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects.GameTaskEffect;
import com.google.common.collect.Multimap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CharmOfFreshStart extends LRCuriosBase {
    private final String finalDescriptionId = Util.makeDescriptionId("item", LegendaryRelics.id("charm_of_end"));

    public CharmOfFreshStart() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        GameTaskEffect gameTaskEffect = LREquipmentEffectTypes.GAME_TASK_CURIO.value().create(0, EquipmentType.CURIO);
        return List.of(gameTaskEffect);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = super.getAttributeModifiers(slotContext, id, stack);
        CuriosApi.addSlotModifier(modifiers, "charm", LegendaryRelics.id("charm_of_fresh_start"), 1, AttributeModifier.Operation.ADD_VALUE);
        return modifiers;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        GameTaskEffect effect = EquipmentEffectAPI.getEffect(stack, LREquipmentEffectTypes.GAME_TASK_CURIO.get());
        if (effect != null && effect.getRawEffectLevel() >= GameTaskEffect.TASKS.size()) {
            return finalDescriptionId;
        }
        return super.getDescriptionId(stack);
    }
}
