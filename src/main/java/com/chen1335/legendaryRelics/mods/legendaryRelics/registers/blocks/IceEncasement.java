package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities.IceEncasementBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 冰封方块：外观就是原版冰，内部可以装一个物品，破坏时把物品掉出来。
 *
 * <p>暂不支持战利品表 —— 内容物的掉落是这里手动 {@code popResource} 的。
 */
public class IceEncasement extends BaseEntityBlock {

    public static final MapCodec<IceEncasement> CODEC = simpleCodec(IceEncasement::new);

    public IceEncasement() {
        this(iceProperties());
    }

    public IceEncasement(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /**
     * 属性照抄原版冰（见 {@code Blocks.ICE}），但去掉 {@code randomTicks()} ——
     * 这个方块没有融化逻辑，不需要随机刻。
     */
    private static BlockBehaviour.Properties iceProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.ICE)
                .friction(0.98F)
                .strength(0.5F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.POLAR_BEAR)
                .isRedstoneConductor((state, level, pos) -> false);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IceEncasementBlockEntity(pos, state);
    }

    /**
     * 仅在创造模式下，右键把手中的物品放进去（内部为空时接受）。
     *
     * <p>放入不消耗手上的物品，与创造模式放置方块的默认行为一致。
     * 取出走"破坏方块"这条路，见 {@link #playerWillDestroy}。
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isCreative()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (!stack.isEmpty()
                && level.getBlockEntity(pos) instanceof IceEncasementBlockEntity encasement
                && encasement.getItem().isEmpty()) {
            if (!level.isClientSide) {
                encasement.setItem(stack.copyWithCount(1));
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /**
     * 玩家破坏方块时把内容物掉出来。
     *
     * <p>用这个钩子而不是 {@code onRemove}：后者在区块卸载时也会触发，
     * 会造成物品凭空掉落甚至刷物品。
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof IceEncasementBlockEntity encasement) {
            ItemStack contained = encasement.getItem();
            if (!contained.isEmpty()) {
                Block.popResource(level, pos, contained);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
