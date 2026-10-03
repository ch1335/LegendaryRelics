package com.chen1335.legendaryRelics.mixinsHooks.legendary_relics;

import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.config.LRServerConfig;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities.IceEncasementBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * 冰刺地物注入的实际逻辑。Mixin 只负责捕获局部变量后转交到这里，本身保持为薄壳。
 *
 * <p>落点选择：冰刺的"腰部中心"是原版 {@code place} 里的局部变量 {@code blockpos}，
 * 原版以它为中心向上下同时展开形状。我们要放的位置是它的正下方一格。
 */
public class IceSpikeFeatureHooks {

    /**
     * 目标格不是浮冰时，最多向上试探的次数。
     *
     * <p>试探顺序：{@code below()} → 腰心 → 腰心上 1 格 → 腰心上 2 格，共 4 个候选位置。
     */
    private static final int MAX_UPWARD_TRIES = 3;

    private IceSpikeFeatureHooks() {
    }

    /**
     * 底部根柱写入某一格之后调用。
     *
     * <p>根柱循环会写很多格，这里靠 {@code writtenPos} 过滤出真正想要的那一格。
     *
     * @param level       世界生成用的世界
     * @param writtenPos  刚刚被写入的坐标
     * @param spikeCenter 冰刺腰部中心（原版局部变量 {@code blockpos}）
     * @param spikeRadius 冰刺最大半径，取值 1~3，对应水平跨度 3~7 格（原版局部变量 {@code j}）
     */
    public static void onBaseColumnSetBlock(WorldGenLevel level, BlockPos writtenPos, BlockPos spikeCenter, int spikeRadius) {
        // 只有"腰部中心正下方"那一格才是目标
        if (!writtenPos.equals(spikeCenter.below())) {
            return;
        }

        BlockPos target = writtenPos;
        // 目标格不是浮冰就往上挪一格再试
        for (int tries = 0; tries < MAX_UPWARD_TRIES && !level.getBlockState(target).is(Blocks.PACKED_ICE); tries++) {
            target = target.above();
        }
        if (!level.getBlockState(target).is(Blocks.PACKED_ICE)) {
            return;
        }

        // 六个方向必须都是完整方块：保证冰封块是被彻底埋住的，而不是露在空气或洞穴里。
        // isCollisionShapeFullBlock 就是原版判定"完整方块"用的方法（isRedstoneConductor 的默认实现）。
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = target.relative(direction);
            if (!level.getBlockState(neighbour).isCollisionShapeFullBlock(level, neighbour)) {
                return;
            }
        }

        // 概率 = 服务端配置的基准值 + 冰刺最大半径 × 服务端配置的每半径增量，越粗的冰刺越容易出。
        double chance = LRServerConfig.ZENITH_ICE_SPIKE_CHANCE.get()
                + LRServerConfig.ZENITH_ICE_SPIKE_CHANCE_PER_RADIUS.get() * spikeRadius;
        // 用位置派生的随机源，而不是地物自己的 randomsource —— 后者在这里多消耗一个随机数，
        // 会让后续根柱的随机跳步与纯原版不一致。
        if (RandomSource.create(Mth.getSeed(target)).nextDouble() >= chance) {
            return;
        }

        level.setBlock(target, LRBlocks.ICE_ENCASEMENT.value().defaultBlockState(), Block.UPDATE_ALL);
        if (level.getBlockEntity(target) instanceof IceEncasementBlockEntity encasement) {
            // setItem 会给内部物品一个随机朝向，所以每处冰刺里的臻冰朝向都不同
            encasement.setItem(new ItemStack(LRItems.ZENITH_ICE.value()));
        }
    }
}
