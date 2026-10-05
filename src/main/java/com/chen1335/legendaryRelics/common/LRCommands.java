package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 战利品模拟指令：反复生成同一张战利品表，按物品类型统计后输出「物品|数量|比例」。
 *
 * <p>用带种子的 getRandomItems 重载，每轮换新种子，避免复用同一份 LootParams 导致每轮结果相同。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LRCommands {
    private static final int DEFAULT_TIMES = 1000;
    private static final int MAX_TIMES = 100000;

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lrsim")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("loot_table", ResourceLocationArgument.id())
                        .suggests(LRCommands::lr$suggestLootTables)
                        .executes(ctx -> lr$simulate(ctx.getSource(),
                                ResourceLocationArgument.getId(ctx, "loot_table"), DEFAULT_TIMES))
                        .then(Commands.argument("times", IntegerArgumentType.integer(1, MAX_TIMES))
                                .executes(ctx -> lr$simulate(ctx.getSource(),
                                        ResourceLocationArgument.getId(ctx, "loot_table"),
                                        IntegerArgumentType.getInteger(ctx, "times"))))));
    }

    private static CompletableFuture<Suggestions> lr$suggestLootTables(CommandContext<CommandSourceStack> ctx,
                                                                      SuggestionsBuilder builder) {
        ctx.getSource().getServer().reloadableRegistries().get()
                .registryOrThrow(Registries.LOOT_TABLE).keySet()
                .forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    }

    /** 用手持物品当 TOOL，让依赖工具的战利品表（算工具附魔/时运那类）也能正常模拟。 */
    private static ItemStack lr$toolOf(CommandSourceStack source) {
        Entity entity = source.getEntity();
        return entity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
    }

    private static int lr$simulate(CommandSourceStack source, ResourceLocation tableId, int times) {
        ServerLevel level = source.getLevel();
        LootTable table = level.getServer().reloadableRegistries().get()
                .registryOrThrow(Registries.LOOT_TABLE).get(tableId);
        if (table == null) {
            source.sendFailure(Component.literal("找不到战利品表：" + tableId));
            return 0;
        }

        // 尽量把常见上下文都塞进去，让玩家的魔法发现属性也能生效；缺参数的战利品表会直接报错而不是静默出错
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, source.getPosition())
                .withParameter(LootContextParams.TOOL, lr$toolOf(source));
        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            builder.withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player);
        }

        LootParams params;
        try {
            params = builder.create(table.getParamSet());
        } catch (Exception e) {
            source.sendFailure(Component.literal("该战利品表需要额外的上下文参数，无法模拟：" + e.getMessage()));
            return 0;
        }

        Map<Item, Integer> counts = new HashMap<>();
        long totalCount = 0L;
        for (int i = 0; i < times; i++) {
            for (ItemStack stack : table.getRandomItems(params, level.getRandom().nextLong())) {
                if (stack.isEmpty()) {
                    continue;
                }
                counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
                totalCount += stack.getCount();
            }
        }

        long totalItems = totalCount;
        source.sendSuccess(() -> Component.literal("模拟 " + times + " 次 " + tableId
                + "：共 " + totalItems + " 个物品，比例 = 物品数量 / 总物品数量，概率 = 物品数量 / 尝试次数"), false);
        counts.entrySet().stream()
                .sorted(Map.Entry.<Item, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    double ratio = totalItems == 0 ? 0.0D : entry.getValue() * 100.0D / totalItems;
                    double probability = entry.getValue() * 100.0D / times;
                    String line = entry.getKey().getDescription().getString()
                            + "|" + entry.getValue()
                            + "|" + String.format(Locale.ROOT, "%.2f%%", ratio)
                            + "|" + String.format(Locale.ROOT, "%.2f%%", probability);
                    source.sendSuccess(() -> Component.literal(line), false);
                });
        return counts.size();
    }
}
