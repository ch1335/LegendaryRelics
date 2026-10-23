package com.chen1335.legendaryRelics.mixinsHooks.legendary_relics;

import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities.IceEncasementBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;

/**
 * 大型冰刺的臻冰放置。每根大冰刺固定生成一个，放在腰部中心正下方，读写时已被根柱埋好。
 */
public class IceSpikeFeatureHooks {

    private IceSpikeFeatureHooks() {
    }

    public static void placeZenithIce(WorldGenLevel level, BlockPos spikeCenter) {
        BlockPos target = spikeCenter.below();
        level.setBlock(target, LRBlocks.ICE_ENCASEMENT.value().defaultBlockState(), Block.UPDATE_ALL);
        if (level.getBlockEntity(target) instanceof IceEncasementBlockEntity encasement) {
            // setItem 会给内部物品一个随机朝向
            encasement.setItem(new ItemStack(LRItems.ZENITH_ICE.value()));
        }
    }
}
