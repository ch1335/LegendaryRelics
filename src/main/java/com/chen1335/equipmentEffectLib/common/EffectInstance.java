package com.chen1335.equipmentEffectLib.common;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import net.minecraft.world.entity.LivingEntity;

public class EffectInstance {
    protected int piece;

    public EffectInstance(int piece) {
        this.piece = piece;
    }

    public final void updatePiece(LivingEntity livingEntity, int piece) {
        this.piece = piece;
        onPieceUpdate(livingEntity, piece);
    }

    public void onPieceUpdate(LivingEntity livingEntity, int piece){

    }

    public int getPiece() {
        return piece;
    }

    public void tick(LivingEntity living) {

    }


    public void onRemove(LivingEntity livingEntity) {
    }

    public CalculatorArg buildArgs(LivingEntity livingEntity) {
        CalculatorArg args = CalculatorArg.simpleArg(livingEntity);
        args.putArg(TieredBonus.TIER, piece);
        return args;
    }
}
