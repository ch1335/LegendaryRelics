package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities;

import com.chen1335.legendaryRelics.API.objects.LRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 冰封方块（暂定名）的方块实体：只负责保存"内部包含的物品"和它的随机朝向。
 *
 * <p>物品与朝向都要同步给客户端，否则渲染器看不到里面有什么。
 * 这里走 {@link #getUpdateTag}/{@link #getUpdatePacket} 的标准通道，
 * 方块侧改动后调用 {@code level.sendBlockUpdated(...)} 即可。
 */
public class IceEncasementBlockEntity extends BlockEntity {

    private static final String ITEM_KEY = "Item";
    private static final String ROTATION_KEY = "Rotation";

    private ItemStack item = ItemStack.EMPTY;

    /** 内部物品的随机朝向（度），让相邻的方块看起来不一样。 */
    private float rotation;

    public IceEncasementBlockEntity(BlockPos pos, BlockState state) {
        super(LRBlockEntities.ICE_ENCASEMENT.value(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public float getRotation() {
        return rotation;
    }

    /**
     * 放入物品，并给它一个随机朝向。
     */
    public void setItem(ItemStack stack) {
        this.item = stack;
        RandomSource random = this.level != null ? this.level.random : RandomSource.create();
        this.rotation = random.nextFloat() * 360.0F;
        setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!item.isEmpty()) {
            tag.put(ITEM_KEY, item.save(registries));
        }
        tag.putFloat(ROTATION_KEY, rotation);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.item = tag.contains(ITEM_KEY)
                ? ItemStack.parse(registries, tag.getCompound(ITEM_KEY)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        this.rotation = tag.getFloat(ROTATION_KEY);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
