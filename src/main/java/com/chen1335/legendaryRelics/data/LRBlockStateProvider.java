package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class LRBlockStateProvider extends BlockStateProvider {
    public LRBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LegendaryRelics.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(LRBlocks.EQUIPMENT_WORKBENCH.value(), models().orientableWithBottom("equipment_workbench",
                LegendaryRelics.id("block/equipment_workbench_side"),
                LegendaryRelics.id("block/equipment_workbench_front"),
                LegendaryRelics.id("block/equipment_workbench_bottom"),
                LegendaryRelics.id("block/equipment_workbench_top")));
    }
}
