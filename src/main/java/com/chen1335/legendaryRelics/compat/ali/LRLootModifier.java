package com.chen1335.legendaryRelics.compat.ali;

import com.chen1335.legendaryRelics.common.lootModifier.LootEntry;
import com.yanny.ali.api.*;
import com.yanny.ali.plugin.common.nodes.GroupNode;
import com.yanny.ali.plugin.common.nodes.ItemNode;
import com.yanny.ali.plugin.common.tooltip.ArrayTooltipNode;
import com.yanny.ali.plugin.common.tooltip.LiteralTooltipNode;
import com.yanny.ali.plugin.server.EntryTooltipUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LRLootModifier implements ILootModifier<ResourceLocation> {
    private String targetTables = "";

    private final List<IOperation> operations = new ArrayList<>();

    public LRLootModifier(IServerUtils utils, String targetTables, List<LootEntry> lootEntries) {
        this.targetTables = targetTables;

//        ArrayTooltipNode.@NotNull Builder tooltipNodeBuilder = ArrayTooltipNode.array();
//        tooltipNodeBuilder.add(EntryTooltipUtils.getLootTableTooltip());
//        tooltipNodeBuilder.add(LiteralTooltipNode.translatable("legendary_relics.rolls_type.independent"));

        ITooltipNode all = EntryTooltipUtils.getLootTableTooltip();
        GroupNode groupNode = new GroupNode(new ArrayList<>(), all);
        for (LootEntry lootEntry : lootEntries) {
            ObjectArrayList<ItemStack> itemStacks = new ObjectArrayList<>();
            lootEntry.stackGetter.accept(itemStacks);
            for (ItemStack itemStack : itemStacks) {
                LootPoolEntryContainer entry = LootItem.lootTableItem(itemStack.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(lootEntry.rolls.value))).build();
                ItemNode itemNode = (ItemNode) utils.getEntryFactory(utils, entry).create(utils, entry, lootEntry.chance.value.floatValue(), 1, List.of(), List.of());
                groupNode.addChildren(itemNode);
            }
        }

        operations.add(new IOperation.AddOperation(itemStack1 -> true, groupNode));
    }


    @Override
    public boolean predicate(@NotNull ResourceLocation value) {
        return Pattern.matches(targetTables, value.toString());
    }

    @Override
    public List<IOperation> getOperations() {
        return operations;
    }

    @Override
    public IType<ResourceLocation> getType() {
        return IType.LOOT_TABLE;
    }
}
