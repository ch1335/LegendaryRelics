package com.chen1335.equipmentEffectLib.slotEffectManagers;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.TreeMultimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class SlotEffectManager {
    private static final List<SlotEffectManager> REGISTERED = new ArrayList<>();
    private final EntityEquipmentEffectData data;


    protected int id;

    public SlotEffectManager(EntityEquipmentEffectData data) {
        this.data = data;
    }

    public static <T extends SlotEffectManager> void register(T manager) {
        manager.id = REGISTERED.size();
        REGISTERED.add(manager);
    }

    protected void onChanged(LivingEntity living, ISlotContext context, @Nonnull ItemStack from, @Nonnull ItemStack to) {
        Map<EffectType<?>, BaseEffect> fromEffects = EquipmentEffectAPI.getEffects(from, getEquipmentType());

        for (Map.Entry<EffectType<?>, BaseEffect> entry : fromEffects.entrySet()) {
            TreeMultimap<Integer, SlotEffectHolder<?>> effectsByType = data.getEffectHoldersByType(entry.getKey());
            effectsByType.values().removeIf(slotEffectHolder -> slotEffectHolder.context().equals(context));
        }

        Map<EffectType<?>, BaseEffect> toEffects = EquipmentEffectAPI.getEffects(to, getEquipmentType());
        for (Map.Entry<EffectType<?>, BaseEffect> entry : toEffects.entrySet()) {
            TreeMultimap<Integer, SlotEffectHolder<?>> effectsByType = data.getEffectHoldersByType(entry.getKey());
            effectsByType.put(entry.getValue().getEffectLevel(living, to), new SlotEffectHolder<>(context, new InfoHolder<>(to, entry.getValue())));
        }


    }

    protected abstract IEquipmentType getEquipmentType();

    public static SlotEffectManager get(int id) {
        if (id < REGISTERED.size()) {
            return REGISTERED.get(id);
        } else {
            throw new RuntimeException("no SlotEffectManager registered with id %s".formatted(id));
        }
    }

    public abstract Map<EffectType<?>, BaseEffect> getEffectsBySlot(ISlotContext context);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SlotEffectManager that = (SlotEffectManager) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
