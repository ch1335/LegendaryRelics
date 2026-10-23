package com.chen1335.legendaryRelics.worldgen.feature;

import com.chen1335.legendaryRelics.mixinsHooks.legendary_relics.IceSpikeFeatureHooks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * 大冰刺：更粗、更矮的冰刺，臻冰只在这里生成（原版冰刺已不再出）。
 */
public class LargeIceSpikeFeature extends Feature<NoneFeatureConfiguration> {

    /** 主体高度 10~12，基准抬高最多 1 格。 */
    private static final int MIN_HEIGHT = 10;
    private static final int HEIGHT_RANGE = 3;

    public LargeIceSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos blockpos = context.origin();
        RandomSource randomsource = context.random();
        WorldGenLevel worldgenlevel = context.level();
        while (worldgenlevel.isEmptyBlock(blockpos) && blockpos.getY() > worldgenlevel.getMinBuildHeight() + 2) {
            blockpos = blockpos.below();
        }
        if (!worldgenlevel.getBlockState(blockpos).is(Blocks.SNOW_BLOCK)) {
            return false;
        }
        blockpos = blockpos.above(randomsource.nextInt(2));
        int height = MIN_HEIGHT + randomsource.nextInt(HEIGHT_RANGE);
        // 半径随高度增长，比原版（height/4 + 0~1）更粗
        int radius = height / 3 + randomsource.nextInt(2);
        for (int k = 0; k < height; ++k) {
            float f = (1.0F - (float) k / (float) height) * (float) radius;
            int l = Mth.ceil(f);
            for (int i1 = -l; i1 <= l; ++i1) {
                float f1 = (float) Mth.abs(i1) - 0.25F;
                for (int j1 = -l; j1 <= l; ++j1) {
                    float f2 = (float) Mth.abs(j1) - 0.25F;
                    if ((i1 != 0 || j1 != 0) && f1 * f1 + f2 * f2 > f * f || (i1 == -l || i1 == l || j1 == -l || j1 == l) && randomsource.nextFloat() > 0.75F) {
                        continue;
                    }
                    BlockState blockstate = worldgenlevel.getBlockState(blockpos.offset(i1, k, j1));
                    if (blockstate.isAir() || isDirt(blockstate) || blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.ICE)) {
                        this.setBlock(worldgenlevel, blockpos.offset(i1, k, j1), Blocks.PACKED_ICE.defaultBlockState());
                    }
                    if (k == 0 || l <= 1 || !(blockstate = worldgenlevel.getBlockState(blockpos.offset(i1, -k, j1))).isAir() && !isDirt(blockstate) && !blockstate.is(Blocks.SNOW_BLOCK) && !blockstate.is(Blocks.ICE)) {
                        continue;
                    }
                    this.setBlock(worldgenlevel, blockpos.offset(i1, -k, j1), Blocks.PACKED_ICE.defaultBlockState());
                }
            }
        }
        int k1 = radius - 1;
        if (k1 < 0) {
            k1 = 0;
        } else if (k1 > 1) {
            k1 = 1;
        }
        for (int l1 = -k1; l1 <= k1; ++l1) {
            for (int i2 = -k1; i2 <= k1; ++i2) {
                BlockState blockstate1;
                BlockPos blockpos1 = blockpos.offset(l1, -1, i2);
                int j2 = 50;
                if (Math.abs(l1) == 1 && Math.abs(i2) == 1) {
                    j2 = randomsource.nextInt(5);
                }
                while (blockpos1.getY() > 50 && ((blockstate1 = worldgenlevel.getBlockState(blockpos1)).isAir() || isDirt(blockstate1) || blockstate1.is(Blocks.SNOW_BLOCK) || blockstate1.is(Blocks.ICE) || blockstate1.is(Blocks.PACKED_ICE))) {
                    this.setBlock(worldgenlevel, blockpos1, Blocks.PACKED_ICE.defaultBlockState());
                    blockpos1 = blockpos1.below();
                    if (--j2 > 0) {
                        continue;
                    }
                    blockpos1 = blockpos1.below(randomsource.nextInt(5) + 1);
                    j2 = randomsource.nextInt(5);
                }
            }
        }
        // 每根大冰刺固定生成一个臻冰，埋进底部根柱
        IceSpikeFeatureHooks.placeZenithIce(worldgenlevel, blockpos);
        return true;
    }
}
