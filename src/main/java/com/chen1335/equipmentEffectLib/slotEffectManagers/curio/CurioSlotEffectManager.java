package com.chen1335.equipmentEffectLib.slotEffectManagers.curio;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.equipmentEffectLib.slotEffectManagers.SlotEffectManager;
import com.google.common.collect.TreeMultimap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CurioSlotEffectManager extends SlotEffectManager {
    public final Map<String, IntObjectMap<Map<EffectType<?>, BaseEffect>>> map = new HashMap<>();

    public CurioSlotEffectManager(EntityEquipmentEffectData data) {
        super(data);
    }

    @Override
    protected IEquipmentType getEquipmentType() {
        return EquipmentType.CURIO;
    }

    public void onCurioChanged(LivingEntity living, String type, int index, @Nonnull ItemStack from, @Nonnull ItemStack to) {
        super.onChanged(living,new SlotContext(type, index),from,to);
    }

    @Override
    public Map<EffectType<?>, BaseEffect> getEffectsBySlot(ISlotContext context) {
        SlotContext context1 = (SlotContext) context;
        return map.computeIfAbsent(context1.type, s -> new IntObjectHashMap<>()).getOrDefault(context1.index, Map.of());
    }

    public record SlotContext(String type, int index) implements ISlotContext {

        @Override
        public Class<? extends SlotEffectManager> getManagerClass() {
            return CurioSlotEffectManager.class;
        }
    }
}
