package com.chen1335.legendaryRelics.common;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TimeLimitedAttributeBonusManager implements INBTSerializable<CompoundTag> {
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
            if (attributeInstance.hasModifier(modifier.id())) {
                attributeInstance.removeModifier(modifier);
            }
            attributeInstance.addTransientModifier(modifier);
            modifierHolders.add(new ModifierHolder(attributeHolder, modifier, time));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        modifierHolders.forEach(modifierHolder -> {
            CompoundTag holderTag = new CompoundTag();
            holderTag.putString("attribute", modifierHolder.attributeHolder.getKey().location().toString());
            holderTag.put("modifier", modifierHolder.modifier.save());
            holderTag.putInt("time", modifierHolder.time);
            listTag.add(holderTag);
        });
        compoundTag.put("modifiers", listTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        ListTag listTag = nbt.getList("modifiers", Tag.TAG_COMPOUND);
        listTag.forEach(tag -> {
            if (tag instanceof CompoundTag compoundTag) {
                BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(compoundTag.getString("attribute"))).ifPresent(holder -> {
                    AttributeModifier modifier = AttributeModifier.load(compoundTag.getCompound("modifier"));
                    modifierHolders.add(new ModifierHolder(holder,modifier,compoundTag.getInt("time")));
                });
            }
        });
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
