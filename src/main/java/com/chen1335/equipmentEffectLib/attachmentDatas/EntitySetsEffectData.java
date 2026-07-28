package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.dataComponentTypes.SetEffectData;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.network.SetsInfoPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntitySetsEffectData {
    public final Map<SetEffect, Integer> pieceInfo = new HashMap<>();

    public final Map<SetEffect, Set<ISlotContext>> equipmentInfo = new HashMap<>();

    public final Map<SetEffect, EffectInstance> effectInstances = new HashMap<>();


    public void update(LivingEntity livingEntity, ISlotContext slotContext, ItemStack from, ItemStack eventTo) {
        SetEffectData fromSetEffectData = EquipmentEffectAPI.getItemSetEffect(from);
        SetEffectData toSetEffectData = EquipmentEffectAPI.getItemSetEffect(eventTo);
        if (fromSetEffectData != null &&slotContext.match(fromSetEffectData.equipmentType().value())) {
            equipmentInfo.computeIfAbsent(fromSetEffectData.setEffect().value(), k -> new HashSet<>()).remove(slotContext);
        }
        if (toSetEffectData != null &&slotContext.match(toSetEffectData.equipmentType().value())) {
            equipmentInfo.computeIfAbsent(toSetEffectData.setEffect().value(), k -> new HashSet<>()).add(slotContext);
        }
        pieceInfo.clear();

        equipmentInfo.forEach((setsEffectBase, slotContexts) -> {
            if (!slotContexts.isEmpty()) {
                pieceInfo.put(setsEffectBase, slotContexts.size());
            }
        });

        Map<SetEffect, EffectInstance> newEffectInstances = new HashMap<>();

        pieceInfo.forEach((setEffect, piece) -> newEffectInstances.put(setEffect, setEffect.createInstance(piece)));

        newEffectInstances.forEach((setEffect, effectInstance) -> {
            EffectInstance oldOrCreated = effectInstances.getOrDefault(setEffect, effectInstance);
            oldOrCreated.updatePiece(livingEntity, effectInstance.getPiece());
            newEffectInstances.put(setEffect, oldOrCreated);
        });

        effectInstances.forEach((setEffect, effectInstance) -> {
            if (!newEffectInstances.containsKey(setEffect)) {
                effectInstance.onRemove(livingEntity);
            }
        });

        effectInstances.clear();
        effectInstances.putAll(newEffectInstances);
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer);
        }
    }

    @Nullable
    public static <T extends EffectInstance> T getSetInstance(LivingEntity livingEntity, SetEffect setEffect, Class<T> clazz) {
        if (!livingEntity.hasData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get())) {
            return null;
        }
        return clazz.cast(livingEntity.getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).effectInstances.get(setEffect));
    }

    @Nullable
    public static EffectInstance getSetInstance(LivingEntity livingEntity, SetEffect setEffect) {
        if (!livingEntity.hasData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get())) {
            return null;
        }
        return livingEntity.getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).effectInstances.get(setEffect);
    }

    public static int getPiece(LivingEntity livingEntity, SetEffect setEffect) {
        EffectInstance setInstance = getSetInstance(livingEntity, setEffect);
        return setInstance == null ? 0 : setInstance.getPiece();
    }

    public void sendToPlayer(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new SetsInfoPack(Map.copyOf(this.pieceInfo)));
    }

    public void tick(LivingEntity living) {
        for (EffectInstance value : effectInstances.values()) {
            value.tick(living);
        }
    }
}
