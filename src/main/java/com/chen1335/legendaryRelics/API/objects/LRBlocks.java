package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.blocks.EquipmentWorkbench;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class LRBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LegendaryRelics.MODID);

    public static final DeferredBlock<EquipmentWorkbench> EQUIPMENT_WORKBENCH = BLOCKS.register("equipment_workbench", EquipmentWorkbench::new);

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    public static Collection<DeferredHolder<Block, ? extends Block>> holders(){
       return BLOCKS.getEntries();
    }
}
