package com.chen1335.legendaryRelics.common;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TimeLimitedAttributeBonusManager {
    private final List<ModifierHolder> modifierHolders = new ArrayList<>();

    public void tick(LivingEntity living) {
        Iterator<ModifierHolder> iterator = modifierHolders.iterator();
        while (iterator.hasNext()) {
            ModifierHolder modifierHolder = iterator.next();
            modifierHolder.time--;
            if (modifierHolder.time <= 0) {
                iterator.remove();
                Objects.requireNonNull(living.getAttribute(modifierHolder.attributeHolder)).removeModifier(modifierHolder.modifier);
            }
        }

    }

    public void addAttributeModifier(LivingEntity living, Holder<Attribute> attributeHolder, AttributeModifier modifier, int time) {
        AttributeInstance attributeInstance = living.getAttribute(attributeHolder);
        if (attributeInstance != null) {
            attributeInstance.addTransientModifier(modifier);
            modifierHolders.add(new ModifierHolder(attributeHolder, modifier, time));
        }
    }

    private static class ModifierHolder {
        private final Holder<Attribute> attributeHolder;
        public final AttributeModifier modifier;
        public int time;

        public ModifierHolder(Holder<Attribute> attributeHolder, AttributeModifier modifier, int time) {
            this.attributeHolder = attributeHolder;
            this.modifier = modifier;
            this.time = time;
        }

    }
}
