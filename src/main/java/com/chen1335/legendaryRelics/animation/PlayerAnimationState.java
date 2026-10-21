package com.chen1335.legendaryRelics.animation;

import com.chen1335.legendaryRelics.common.PlayerAnimationHandler;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class PlayerAnimationState {
    private final Float2ObjectMap<List<KeyframeAction>> map = new Float2ObjectArrayMap<>();
    private BooleanSupplier checker = () -> true;
    private final float animationTime;
    private float time;
    private float speed = 1;

    public PlayerAnimationState(float time) {
        this.animationTime = time;
    }

    public void tick(Player player) {
        for (Float2ObjectMap.Entry<List<KeyframeAction>> next : map.float2ObjectEntrySet()) {
            if (time >= next.getFloatKey()) {
                for (KeyframeAction keyframeAction : next.getValue()) {
                    if (!keyframeAction.trigger) {
                        keyframeAction.trigger = true;
                        keyframeAction.trigger(this);
                    }
                }
            }
        }
        boolean b = !checker.getAsBoolean();
        if (time >= animationTime || b) {
            if (b) {
                PlayerAnimationHandler.clean(player, 2);
            }
            LRPlayerData.getData(player).playerAnimationState = null;
        }
        time = time + speed;
    }

    public static PlayerAnimationState start(float time) {
        return new PlayerAnimationState(time);
    }

    public void restTime() {
        time = 0;
    }

    public void setTime(float time) {
       this.time = time;
    }

    public void resetKeyframe() {
        map.values().forEach(l -> l.forEach(KeyframeAction::rest));
    }

    public PlayerAnimationState addKeyframeAction(float time, KeyframeAction keyframeAction) {
        map.computeIfAbsent(time, f -> new ArrayList<>()).add(keyframeAction);
        return this;
    }

    public PlayerAnimationState checker(BooleanSupplier supplier) {
        checker = supplier;
        return this;
    }
}
