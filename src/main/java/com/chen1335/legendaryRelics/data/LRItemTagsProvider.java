package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRTags;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosTags;

import java.util.concurrent.CompletableFuture;

public class LRItemTagsProvider extends ItemTagsProvider {
    public LRItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(LRTags.Items.CAN_ONLY_WEAR_ONE).add(
                LRItems.CHARM_OF_FRESH_START.asItem()
        );

        // 末影龙鳞。ISS 是可选依赖，用 optional 条目，缺失时不会拖垮整个标签。
        tag(LRTags.Items.SCALES_ENDER_DRAGON)
                .add(LRItems.DRAGON_SCALE.asItem())
                .addOptional(ItemRegistry.DRAGONSKIN.getId());

        tag(CuriosTags.CHARM)
                .add(
                        LRItems.AGGLOMERATION_MALICE.asItem(),
                        LRItems.NETHER_TALISMAN.asItem(),
                        LRItems.PURGATORY_TALISMAN.asItem(),
                        LRItems.CHARM_OF_FRESH_START.asItem()
                );

        tag(CuriosTags.RING)
                .add(
                        LRItems.HARDENED_RING.asItem(),
                        LRItems.THE_ORE_COLLECTORS_RING.asItem(),
                        LRItems.LAVA_RING.asItem(),
                        LRItems.NETHER_RING.asItem()
                );

        tag(CuriosTags.HANDS)
                .add(
                        LRItems.DARK_STEEL_CLAW.asItem()
                );

        tag(CuriosTags.BELT)
                .add(
                        LRItems.SHIELD_REGENERATOR.asItem()
                );

        tag(CuriosTags.NECKLACE).add(
                LRItems.SACRED_TALISMAN.asItem(),
                LRItems.PERSEVERANCE_NECKLACE.asItem(),
                LRItems.THUNDERBOLT_NECKLACE.asItem(),
                LRItems.FROSTHEART_PENDANT.asItem(),
                LRItems.HEALING_TALISMAN.asItem()
        );

        // 神圣斗篷同时占用背饰与斗篷两个槽位
        tag(CuriosTags.BACK).add(
                LRItems.HOLY_CLOAK.asItem()
        );

        tag(CuriosTags.createItemTag("cloak")).add(
                LRItems.HOLY_CLOAK.asItem(),
                LRItems.SNOWFEATHER_CLOAK.asItem()
        );

        // ISS 物品只在装有时才注册，必须用 optional 条目，否则缺失时标签解析失败
        tag(ItemTags.HEAD_ARMOR).add(
                LRItems.BLACK_DRAGON_HELMET.value(),
                LRItems.INFERNO_HELMET.value(),
                LRItems.TWISTED_HELMET.value()
        ).addOptional(ISSRegisters.Item.SOUL_HELMET.getId());

        tag(ItemTags.CHEST_ARMOR).add(
                LRItems.BLACK_DRAGON_CHEST_PLATE.value(),
                LRItems.INFERNO_CHEST_PLATE.value(),
                LRItems.TWISTED_CHEST_PLATE.value()
        ).addOptional(ISSRegisters.Item.SOUL_CHEST_PLATE.getId());

        tag(ItemTags.LEG_ARMOR).add(
                LRItems.BLACK_DRAGON_LEGGINGS.value(),
                LRItems.INFERNO_LEGGINGS.value(),
                LRItems.TWISTED_LEGGINGS.value()
        ).addOptional(ISSRegisters.Item.SOUL_LEGGINGS.getId());

        tag(ItemTags.FOOT_ARMOR).add(
                LRItems.BLACK_DRAGON_BOOTS.value(),
                LRItems.INFERNO_BOOTS.value(),
                LRItems.TWISTED_BOOTS.value()
        ).addOptional(ISSRegisters.Item.SOUL_BOOTS.getId());

        tag(ItemTags.SWORDS).add(
                LRItems.WITHERING_BLADE.value(),
                LRItems.REAPER.value(),
                LRItems.SKELETON_THROWING_KNIFE.asItem(),
                LRItems.SHADOW_DAGGER.value(),
                LRItems.FROST_SHARD.value(),
                LRItems.BLAZING_CHAKRAM.value(),
                LRItems.ICEBREAKER.value(),
                LRItems.FROST_STEEL_SWORD.value()
        ).addOptional(ISSRegisters.Item.MAGIC_BREAKER.getId());

        tag(Tags.Items.TOOLS_BOW).add(
                LRItems.LAST_WHISPER.asItem()
        );

        tag(ItemTags.BOW_ENCHANTABLE).add(
                LRItems.LAST_WHISPER.asItem()
        );

        tag(ItemTags.DURABILITY_ENCHANTABLE).add(
                LRItems.LAST_WHISPER.asItem()
        );

        tag(ItemTags.VANISHING_ENCHANTABLE).add(
                LRItems.LAST_WHISPER.asItem()
        );

        tag(Tags.Items.RANGED_WEAPON_TOOLS).add(
                LRItems.LAST_WHISPER.asItem()
        );
    }
}
