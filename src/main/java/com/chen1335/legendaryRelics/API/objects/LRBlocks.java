package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.EquipmentWorkbench;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.IceEncasement;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class LRBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LegendaryRelics.MODID);

    public static final DeferredBlock<EquipmentWorkbench> EQUIPMENT_WORKBENCH = BLOCKS.register("equipment_workbench", EquipmentWorkbench::new);

    /** 冰封方块：内部可装一个物品，破坏时掉落内容物。 */
    public static final DeferredBlock<IceEncasement> ICE_ENCASEMENT = BLOCKS.register("ice_encasement", () -> new IceEncasement());

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    public static Collection<DeferredHolder<Block, ? extends Block>> holders(){
       return BLOCKS.getEntries();
    }
}
