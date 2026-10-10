package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
import java.util.List;
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
    /** 百分比列的固定宽度，100.00 加上百分号正好占 7 个西文字宽。 */
    private static final int WIDTH_PERCENT = 7;

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

    /**
     * 照着原版 loot 指令用 suggestResource：它会同时给出带命名空间和不带的两种写法，
     * 否则输入 desert 时匹配不到 minecraft:chests/desert_pyramid，补全就出不来了。
     */
    private static CompletableFuture<Suggestions> lr$suggestLootTables(CommandContext<CommandSourceStack> ctx,
                                                                      SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(
                ctx.getSource().getServer().reloadableRegistries().get()
                        .registryOrThrow(Registries.LOOT_TABLE).keySet(),
                builder);
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

        // 尽量把常见上下文都塞进去：玩家身份（魔法发现）、手持物（工具附魔）、玩家攻击的伤害来源（实体掉落）
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, source.getPosition())
                .withParameter(LootContextParams.TOOL, lr$toolOf(source));
        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            builder.withParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player));
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

        List<Map.Entry<Item, Integer>> sorted = counts.entrySet().stream()
                .sorted(Map.Entry.<Item, Integer>comparingByValue().reversed())
                .toList();
        int countWidth = String.valueOf(sorted.isEmpty() ? 0 : sorted.getFirst().getValue()).length();
        int nameWidth = sorted.stream()
                .mapToInt(entry -> lr$width(entry.getKey().getDescription().getString()))
                .max().orElse(0) + 2;

        source.sendSuccess(() -> Component.literal(
                lr$pad("物品", nameWidth) + lr$padLeft("数量", countWidth)
                        + "  " + lr$padLeft("比例", WIDTH_PERCENT)
                        + "  " + lr$padLeft("概率", WIDTH_PERCENT)), false);

        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<Item, Integer> entry = sorted.get(i);
            double ratio = totalItems == 0 ? 0.0D : entry.getValue() * 100.0D / totalItems;
            double probability = entry.getValue() * 100.0D / times;
            String name = entry.getKey().getDescription().getString();
            int color = lr$colorOf(i);

            // 颜色不占字形宽度，所以补位空格紧跟名字，对齐不受影响
            String rest = " ".repeat(Math.max(0, nameWidth - lr$width(name)))
                    + lr$padLeft(String.valueOf(entry.getValue()), countWidth)
                    + "  " + lr$padLeft(String.format(Locale.ROOT, "%.2f%%", ratio), WIDTH_PERCENT)
                    + "  " + lr$padLeft(String.format(Locale.ROOT, "%.2f%%", probability), WIDTH_PERCENT);

            Component line = Component.empty()
                    .append(Component.literal(name).withStyle(style -> style.withColor(color)))
                    .append(rest);
            source.sendSuccess(() -> line, false);
        }
        return counts.size();
    }

    /**
     * 按列表顺序分配色相：黄金角步进，相邻条目在色环上永远隔得最开，也不会因为条目多而挤在一起。
     * 固定饱和度与亮度，保证在聊天栏深色背景上都读得清。
     */
    private static int lr$colorOf(int index) {
        float hue = (index * 0.618033988749895F) % 1.0F;
        return Mth.hsvToRgb(hue, 0.65F, 0.95F);
    }
    /** 中文字形约为西文两倍宽，按显示宽度补空格才能真正竖着对齐。 */
    private static int lr$width(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            width += text.charAt(i) > 0x2E80 ? 2 : 1;
        }
        return width;
    }

    private static String lr$pad(String text, int width) {
        return text + " ".repeat(Math.max(0, width - lr$width(text)));
    }

    private static String lr$padLeft(String text, int width) {
        return " ".repeat(Math.max(0, width - lr$width(text))) + text;
    }
}
