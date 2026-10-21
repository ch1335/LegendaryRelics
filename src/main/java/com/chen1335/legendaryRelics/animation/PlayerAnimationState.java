package com.chen1335.legendaryRelics.animation;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAnimationState {
    private final Float2ObjectMap<List<KeyframeAction>> map = new Float2ObjectArrayMap<>();

    private final float animationTime;
    private float time;
    private float speed = 1;

    public PlayerAnimationState(float time) {
        this.animationTime = time;
    }

    public void tick(Player player) {
        ObjectIterator<Float2ObjectMap.Entry<List<KeyframeAction>>> iterator = map.float2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Float2ObjectMap.Entry<List<KeyframeAction>> next = iterator.next();
            if (time >= next.getFloatKey()) {
                for (KeyframeAction keyframeAction : next.getValue()) {
                    keyframeAction.run(player);
                }
                iterator.remove();
            }
        }
        if (time >= animationTime) {
            LRPlayerData.getData(player).playerAnimationState = null;
        }
        time = time + speed;
    }

    public static PlayerAnimationState start(float time) {
        return new PlayerAnimationState(time);
    }

    public PlayerAnimationState addKeyframeAction(float time, KeyframeAction keyframeAction) {
        map.computeIfAbsent(time, f -> new ArrayList<>()).add(keyframeAction);
        return this;
    }
}
