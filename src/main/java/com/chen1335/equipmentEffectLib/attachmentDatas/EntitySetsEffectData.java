package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.chen1335.legendaryRelics.network.SetsInfoPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class EntitySetsEffectData {
    public final Map<SetsEffectBase, Integer> setsEffectData = new HashMap<>();

    public void update(LivingEntity livingEntity) {
        Map<SetsEffectBase, Set<Item>> setMap = new HashMap<>();
        for (IEquipmentSource equipmentSource : EquipmentEffectLib.EQUIPMENT_SOURCES) {
            buildSets(equipmentSource.get(livingEntity), setMap);
        }
        setsEffectData.clear();
        setMap.forEach((setsEffectBase, items) -> {
            setsEffectData.put(setsEffectBase, items.size());
        });

        if (livingEntity instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer);
        }
    }

    public static void buildSets(List<ItemStack> itemStacks, Map<SetsEffectBase, Set<Item>> map) {
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) {
                SetsEffectBase setsEffectBase = ((IEEItemExtension) itemStack.getItem()).EE$GetSetsEffect();
                if (setsEffectBase != null) {
                    map.computeIfAbsent(setsEffectBase, setsEffectBase1 -> new HashSet<>()).add(itemStack.getItem());
                }
            }
        }
    }

    public void sendToPlayer(ServerPlayer serverPlayer){
        PacketDistributor.sendToPlayer(serverPlayer, new SetsInfoPack(this.setsEffectData));
    }
}
