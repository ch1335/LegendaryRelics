package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.IArmorEffect;
import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentSources.Hand;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEquipmentEffectData {


    public static class InfoHolder<T extends BaseEffect> {
        private ItemStack itemStack;
        private T effect;

        public InfoHolder(ItemStack itemStack, T effect) {
            this.itemStack = itemStack;
            this.effect = effect;
        }

        public T effect() {
            return effect;
        }

        public ItemStack itemStack() {
            return itemStack;
        }

        private void setEffect(T effect) {
            this.effect = effect;
        }

        private void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    private final Map<IEquipmentType, Map<EffectType<?>, List<InfoHolder<?>>>> effects = new HashMap<>();

    public List<InfoHolder<?>> collectAllEffects() {
        List<InfoHolder<?>> list = new ArrayList<>();
        for (Map.Entry<IEquipmentType, Map<EffectType<?>, List<InfoHolder<?>>>> entry : effects.entrySet()) {
            for (Map.Entry<EffectType<?>, List<InfoHolder<?>>> entry1 : entry.getValue().entrySet()) {
                list.addAll(entry1.getValue());
            }
        }
        return ImmutableList.copyOf(list);
    }

    public Map<EffectType<?>, List<InfoHolder<?>>> getEffects(IEquipmentType equipmentType) {
        return effects.getOrDefault(equipmentType, Map.of());
    }

    @Nullable
    public <T extends BaseEffect> List<InfoHolder<T>> getEffectsByType(EffectType<T> effectType) {
        return Cast.cast(getEffects(effectType.getEquipmentType()).get(effectType));
    }

    //快速更新同一个itemStack的效果
    public void updateSameItem(IEquipmentType equipmentType, ItemStack from, ItemStack to) {
        for (Map.Entry<EffectType<?>, List<InfoHolder<?>>> entry : getEffects(equipmentType).entrySet()) {
            for (InfoHolder<?> infoHolder : entry.getValue()) {
                if (ItemStack.matches(infoHolder.itemStack, from)) {
                    infoHolder.setItemStack(to);
                }
            }
        }

    }

    public void tick(LivingEntity living) {
        for (List<EntityEquipmentEffectData.InfoHolder<?>> value : getEffects(EquipmentType.CURIO).values()) {
            for (EntityEquipmentEffectData.InfoHolder<?> info : value) {
                if (info.effect() instanceof ICurioEffect curioEffect) {
                    curioEffect.curioTick(info.itemStack(), living);
                }
            }
        }

        for (List<EntityEquipmentEffectData.InfoHolder<?>> value : getEffects(EquipmentType.ARMOR).values()) {
            for (EntityEquipmentEffectData.InfoHolder<?> info : value) {
                if (info.effect() instanceof IArmorEffect armorEffect) {
                    armorEffect.armorTick(info.itemStack(), living);
                }
            }
        }
    }

    //更新所有效果
    public void update(LivingEntity entity, IEquipmentType equipmentType) {
        if (equipmentType.getSource() == null) {
            return;
        }
        List<ItemStack> itemStacks = equipmentType.getSource().get(entity);
        Map<EffectType<?>, List<InfoHolder<?>>> newEffects = new HashMap<>();


        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty() && itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA)) {
                for (Map.Entry<EffectType<?>, BaseEffect> entry : EquipmentEffectAPI.getEffects(itemStack).entrySet()) {
                    EffectType<?> effectType = entry.getKey();
                    BaseEffect baseEffect = entry.getValue();
                    if (effectType.getEquipmentType() == equipmentType) {
                        if (effectType.isStackable()) {
                            newEffects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()).add(new InfoHolder<>(itemStack, baseEffect));
                        } else {
                            List<InfoHolder<?>> oldInfos = newEffects.get(effectType);
                            if (oldInfos == null) {
                                newEffects.put(effectType, List.of(new InfoHolder<>(itemStack, baseEffect)));
                            } else {
                                InfoHolder<?> oldInfo = oldInfos.getFirst();
                                if (baseEffect.isBetterThan(entity, itemStack, oldInfo.effect, oldInfo.itemStack)) {
                                    newEffects.put(effectType, List.of(new InfoHolder<>(itemStack, baseEffect)));
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<EffectType<?>, List<InfoHolder<?>>> oldEffects = effects.getOrDefault(equipmentType, Map.of());

        for (Map.Entry<EffectType<?>, List<InfoHolder<?>>> entry : oldEffects.entrySet()) {
            if (!newEffects.containsKey(entry.getKey())) {
                for (InfoHolder<?> infoHolder : entry.getValue()) {
                    infoHolder.effect.onDeActive(entity, infoHolder.itemStack);
                }
            }
        }

        for (Map.Entry<EffectType<?>, List<InfoHolder<?>>> entry : newEffects.entrySet()) {
            List<InfoHolder<?>> oldInfoHolders = oldEffects.get(entry.getKey());
            if (oldInfoHolders == null) {
                for (InfoHolder<?> infoHolder : entry.getValue()) {
                    infoHolder.effect.onActive(entity, infoHolder.itemStack);
                }
            } else {
                List<InfoHolder<?>> newInfoHolders = entry.getValue();
                for (InfoHolder<?> oldInfoHolder : oldInfoHolders) {
                    if (!newInfoHolders.contains(oldInfoHolder)) {
                        oldInfoHolder.effect.onDeActive(entity, oldInfoHolder.itemStack);
                    }
                }
                for (InfoHolder<?> newInfoHolder : newInfoHolders) {
                    if (!oldInfoHolders.contains(newInfoHolder)) {
                        newInfoHolder.effect.onActive(entity, newInfoHolder.itemStack);
                    }
                }
            }
        }
        effects.put(equipmentType, newEffects);
    }

    public interface IEquipmentType {
        IEquipmentSource getSource();
    }

    public enum EquipmentType implements IEquipmentType {
        CURIO(CuriosSource.INSTANCE),
        ARMOR(ArmorSource.INSTANCE),
        HAND(Hand.INSTANCE);
        private final IEquipmentSource source;

        EquipmentType(IEquipmentSource source) {
            this.source = source;
        }

        @Override
        public IEquipmentSource getSource() {
            return source;
        }
    }
}
