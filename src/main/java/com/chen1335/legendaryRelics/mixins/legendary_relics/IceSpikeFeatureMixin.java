package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsHooks.legendary_relics.IceSpikeFeatureHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.IceSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
/**
 * 让原版冰刺在腰部中心偏下的位置生成一个冰封块，里面装着一块臻冰。
 *
 * <p>实际逻辑在 {@link IceSpikeFeatureHooks}，本类只负责捕获局部变量并转交。
 */
@Mixin(IceSpikeFeature.class)
public class IceSpikeFeatureMixin {

    @WrapOperation(method = "place",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/IceSpikeFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
                    ordinal = 2))
    private void legendary_relics$placeZenithIceEncasement(IceSpikeFeature instance, LevelWriter levelWriter,
                                                           BlockPos pos, BlockState state, Operation<Void> original,
                                                           @Local(ordinal = 0) BlockPos spikeCenter,
                                                           @Local(ordinal = 1) int spikeRadius) {
        original.call(instance, levelWriter, pos, state);

        // 逻辑见 IceSpikeFeatureHooks
        if (levelWriter instanceof WorldGenLevel level) {
            IceSpikeFeatureHooks.onBaseColumnSetBlock(level, pos, spikeCenter, spikeRadius);
        }
    }
}
