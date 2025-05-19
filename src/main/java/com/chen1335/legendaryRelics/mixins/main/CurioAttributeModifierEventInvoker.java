package com.chen1335.legendaryRelics.mixins.main;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@Mixin(CurioAttributeModifierEvent.class)
public interface CurioAttributeModifierEventInvoker {
    @Invoker("getModifiableMap")
    Multimap<Holder<Attribute>, AttributeModifier> lr$getModifiableMap();
}
