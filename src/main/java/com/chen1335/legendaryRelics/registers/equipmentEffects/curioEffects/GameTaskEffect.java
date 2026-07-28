package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.ISubEffectProvider;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.misc.gameTask.taskTypes.*;
import com.chen1335.legendaryRelics.utils.SimpleSchedule;
import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class GameTaskEffect extends LRCurioEffect implements ISubEffectProvider<GameTaskEffect> {
    public static List<AttributeEntire> ATTRIBUTES;

    public static List<ITask> TASKS;

    private final BaseEffect allAttributeBoost = LREquipmentEffectTypes.ALL_ATTRIBUTE_BOOST.get().create(1, EquipmentTypes.CURIO);

    public GameTaskEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        int rawEffectLevel = getRawEffectLevel();
        if (rawEffectLevel < TASKS.size()) {
            Component taskComponent = TASKS.get(rawEffectLevel).getComponent();
            tooltipComponents.add(Component.translatable("legendary_relics.task").append(taskComponent));
            if (rawEffectLevel == TASKS.size() - 1) {
                tooltipComponents.add(Component.translatable("legendary_relics.task.transfer").withStyle(ChatFormatting.GRAY));
            } else if (rawEffectLevel < TASKS.size()) {
                tooltipComponents.add(Component.translatable("legendary_relics.task_reward").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        if (entity instanceof Player player) {
            SimpleSchedule.addSchedule(player.level(),new SimpleSchedule.Wait(()->finishTask(player, new CustomTask(LegendaryRelics.id("wear_curio")), itemStack),1));
        }
    }

    public static void tryFinishTask(Player player, ITask task) {
        LREquipmentEffectTypes.GAME_TASK_CURIO.value().findBestEffect(player).ifPresent(slotHolder -> {
            slotHolder.infoHolder().effect().finishTask(player, task, slotHolder.infoHolder().itemStack());
        });
    }

    public void finishTask(Player player, ITask task, ItemStack itemStack) {
        int rawEffectLevel = getRawEffectLevel();
        if (TASKS.size() > rawEffectLevel) {
            ITask task1 = TASKS.get(rawEffectLevel);
            if (task1.check(task)) {
                player.sendSystemMessage(Component.translatable("legendary_relics.task.has_finished", task1.getComponent().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GOLD));
                onTaskFinished(player, task, itemStack);
            }
        }
    }


    public void onTaskFinished(Player player, ITask task, ItemStack itemStack) {
        ItemEffectsData itemEffectsData = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT);
        if (itemEffectsData != null) {
            LinkedHashMap<EffectType<?>, BaseEffect> map = new LinkedHashMap<>(itemEffectsData.effects());
            GameTaskEffect copy = (GameTaskEffect) this.copy();
            copy.setRawEffectLevel(getRawEffectLevel() + 1);
            map.put(getType(),copy);
            itemStack.set(EEItemDataComponentTypes.ITEM_EFFECT,ItemEffectsData.buildFromLinkedMap(map));
        }
    }

    @Override
    public void modifyCurioAttribute(CurioAttributeModifierEvent event) {
        for (AttributeEntire attribute : ATTRIBUTES) {
            double amount;
            if (getRawEffectLevel() < attribute.amounts().size()) {
                amount = attribute.amounts.get(getRawEffectLevel());
            } else {
                amount = attribute.amounts.getLast();
            }
            event.addModifier(attribute.attribute, new AttributeModifier(LegendaryRelics.id("task_attribute_bonus"), amount, attribute.operation));
        }
    }

    public static void PlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        tryFinishTask(event.getEntity(), TravelToDimTask.of(event.getTo()));
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            tryFinishTask(player, KillEntityTask.of(event.getEntity().getType()));
        }
    }

    public static void initTasks() {
        ImmutableList.Builder<AttributeEntire> builder = getAttributeEntriesBuilder();

        ATTRIBUTES = builder.build();

        TASKS = List.of(
                CustomTask.of("wear_curio"),
                GainItemTask.of(Items.DIAMOND),
                TravelToDimTask.of(Level.NETHER),
                GainItemTask.of(Items.ENDER_EYE),
                TravelToDimTask.of(Level.END),
                KillEntityTask.of(EntityType.ENDER_DRAGON)
        );


        File file = LegendaryRelics.CONFIGS_PATH.resolve("task_config.json").toFile();


        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                JsonObject jsonElement = JsonParser.parseReader(fileReader).getAsJsonObject();
                JsonElement tasks = jsonElement.get("tasks");
                JsonElement attributes = jsonElement.get("attributes");


                DataResult<Pair<List<ITask>, JsonElement>> decode1 = ITask.Tasks.CODEC.listOf().decode(JsonOps.INSTANCE, tasks);
                if (decode1.isSuccess()) {
                    TASKS = decode1.getOrThrow().getFirst();
                } else {
                    saveToFile(file);
                    return;
                }
                DataResult<Pair<List<AttributeEntire>, JsonElement>> decode = AttributeEntire.CODEC.listOf().decode(JsonOps.INSTANCE, attributes);
                if (decode.isSuccess()) {
                    ATTRIBUTES = decode.getOrThrow().getFirst();
                } else {
                    saveToFile(file);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            saveToFile(file);
        }
    }

    public static void saveToFile(File file) {
        JsonObject main = new JsonObject();
        JsonElement tasks = ITask.Tasks.CODEC.listOf().encodeStart(JsonOps.INSTANCE, TASKS).getOrThrow();
        main.add("tasks", tasks);
        JsonElement attributes = AttributeEntire.CODEC.listOf().encodeStart(JsonOps.INSTANCE, ATTRIBUTES).getOrThrow();
        main.add("attributes", attributes);
        try (FileWriter fileWriter = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(main, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static ImmutableList.@NotNull Builder<AttributeEntire> getAttributeEntriesBuilder() {
        ImmutableList.Builder<AttributeEntire> builder = ImmutableList.builder();
        builder.add(new AttributeEntire(Attributes.MAX_HEALTH, List.of(0D, 0.5D, 1D, 2D, 3D, 4D, 5D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(Attributes.ARMOR, List.of(0D, 0.5D, 1D, 2D, 3D, 4D, 5D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(Attributes.ATTACK_DAMAGE, List.of(0D, 0.005D, 0.01D, 0.02D, 0.03D, 0.04D, 0.05D), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.add(new AttributeEntire(AttributesGetter.projectDamage(), List.of(0D, 0.005D, 0.01D, 0.02D, 0.03D, 0.04D, 0.05D), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        builder.add(new AttributeEntire(AttributesGetter.armorPierce(), List.of(0D, 0.25D, 0.5D, 1D, 1.5D, 2D, 2.5D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(AttributesGetter.lifeSteal(), List.of(0D, 0.0025D, 0.005D, 0.01D, 0.015D, 0.02D, 0.025D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(AttributesGetter.critDamame(), List.of(0D, 0.005D, 0.01D, 0.02D, 0.03D, 0.04D, 0.05D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(AttributesGetter.critChance(), List.of(0D, 0.005D, 0.01D, 0.02D, 0.03D, 0.04D, 0.05D), AttributeModifier.Operation.ADD_VALUE));
        builder.add(new AttributeEntire(AttributesGetter.miningSpeed(), List.of(0D, 0.01D, 0.02D, 0.04D, 0.06D, 0.08D, 0.1D), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return builder;
    }

    @Override
    public List<BaseEffect> getSubEffects(ItemStack itemStack) {
        if (getRawEffectLevel() >= TASKS.size()) {
            return List.of(allAttributeBoost);
        }
        return List.of();
    }

    @Override
    public void copyFrom(GameTaskEffect provider) {

    }

    public record AttributeEntire(Holder<Attribute> attribute, List<Double> amounts,
                                  AttributeModifier.Operation operation) {
        public static Codec<AttributeEntire> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeEntire::attribute),
                        Codec.DOUBLE.listOf().fieldOf("amounts").forGetter(AttributeEntire::amounts),
                        AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEntire::operation)
                ).apply(instance, AttributeEntire::new)
        );
    }
}
