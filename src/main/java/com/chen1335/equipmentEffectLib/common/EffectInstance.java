package com.chen1335.equipmentEffectLib.common;

import net.minecraft.world.entity.LivingEntity;

public class EffectInstance {
    protected int piece;

    public EffectInstance(int piece) {
        this.piece = piece;
    }

    public void updatePiece(LivingEntity livingEntity, int piece){
        this.piece = piece;
    }

    public int getPiece() {
        return piece;
    }

    public void tick(LivingEntity living) {

    }


    public void onRemove(LivingEntity livingEntity) {
    }
}
