package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities.IceEncasementBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体类型注册。
 *
 * <p>本模组之前没有方块实体，这是第一个，所以单独开一个注册类（与 {@link LRBlocks} 同样的风格）。
 */
public class LRBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LegendaryRelics.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IceEncasementBlockEntity>> ICE_ENCASEMENT =
            BLOCK_ENTITIES.register("ice_encasement", () -> BlockEntityType.Builder
                    .of(IceEncasementBlockEntity::new, LRBlocks.ICE_ENCASEMENT.value())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
