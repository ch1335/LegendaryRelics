package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.common.SetEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
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

    public final Map<SetEffect, EffectInstance> effectInstances = new HashMap<>();


    public void update(LivingEntity livingEntity) {
        Map<SetEffect, Set<Item>> setMap = new HashMap<>();
        for (EquipmentType equipmentType : EquipmentType.getUnits().values()) {
            buildSets(equipmentType,equipmentType.source().get(livingEntity), setMap);
        }
        pieceInfo.clear();
        setMap.forEach((setsEffectBase, items) -> {
            pieceInfo.put(setsEffectBase, items.size());
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

    public static void buildSets(EquipmentType equipmentType, List<ItemStack> itemStacks, Map<SetEffect, Set<Item>> map) {
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) {
                SetEffectHolder setEffectHolder = ((IEEItemExtension) itemStack.getItem()).EE$GetSetsEffect();
                if (setEffectHolder != null && setEffectHolder.equipmentType() == equipmentType) {
                    map.computeIfAbsent(setEffectHolder.setEffect(), setsEffectBase1 -> new HashSet<>()).add(itemStack.getItem());
                }
            }
        }
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
