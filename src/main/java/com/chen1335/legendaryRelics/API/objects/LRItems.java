package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonBoots;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonChestPlate;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonHelmet;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonLeggings;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.infernoSet.InfernoBoots;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.infernoSet.InfernoChestPlate;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.infernoSet.InfernoHelmet;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.infernoSet.InfernoLeggings;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet.TwistedBoots;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet.TwistedChestPlate;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet.TwistedHelmet;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet.TwistedLeggings;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios.*;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.misc.*;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class LRItems {
    private static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(LegendaryRelics.MODID);

    public static void register(IEventBus modEventBus) {
        ITEM_DEFERRED_REGISTER.register(modEventBus);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEM_DEFERRED_REGISTER.getEntries();
    }

    public static final DeferredItem<SacredTalisman> SACRED_TALISMAN = ITEM_DEFERRED_REGISTER.register("sacred_talisman", SacredTalisman::new);

    public static final DeferredItem<AgglomerationMalice> AGGLOMERATION_MALICE = ITEM_DEFERRED_REGISTER.register("agglomeration_malice", AgglomerationMalice::new);

    public static final DeferredItem<ShieldRegenerator> SHIELD_REGENERATOR = ITEM_DEFERRED_REGISTER.register("shield_regenerator", ShieldRegenerator::new);

    public static final DeferredItem<HardenedRing> HARDENED_RING = ITEM_DEFERRED_REGISTER.register("hardened_ring", HardenedRing::new);

    public static final DeferredItem<DarkSteelClaw> DARK_STEEL_CLAW = ITEM_DEFERRED_REGISTER.register("dark_steel_claw", DarkSteelClaw::new);

    public static final DeferredItem<BlackDragonHelmet> BLACK_DRAGON_HELMET = ITEM_DEFERRED_REGISTER.register("black_dragon_helmet", BlackDragonHelmet::new);

    public static final DeferredItem<BlackDragonChestPlate> BLACK_DRAGON_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("black_dragon_chestplate", BlackDragonChestPlate::new);

    public static final DeferredItem<BlackDragonLeggings> BLACK_DRAGON_LEGGINGS = ITEM_DEFERRED_REGISTER.register("black_dragon_leggings", BlackDragonLeggings::new);

    public static final DeferredItem<BlackDragonBoots> BLACK_DRAGON_BOOTS = ITEM_DEFERRED_REGISTER.register("black_dragon_boots", BlackDragonBoots::new);

    public static final DeferredItem<DarkGoldForgingTool> DARK_GOLD_FORGING_TOOL = ITEM_DEFERRED_REGISTER.register("dark_gold_forging_tool", DarkGoldForgingTool::new);

    public static final DeferredItem<AncientFragment> ANCIENT_FRAGMENT = ITEM_DEFERRED_REGISTER.register("ancient_fragment", AncientFragment::new);

    public static final DeferredItem<TheOreCollectorsRing> THE_ORE_COLLECTORS_RING = ITEM_DEFERRED_REGISTER.register("the_ore_collectors_ring", TheOreCollectorsRing::new);

    public static final DeferredItem<LavaRing> LAVA_RING = ITEM_DEFERRED_REGISTER.register("lava_ring", LavaRing::new);

    public static final DeferredItem<NetherRing> NETHER_RING = ITEM_DEFERRED_REGISTER.register("nether_ring", NetherRing::new);

    public static final DeferredItem<NetherTalisman> NETHER_TALISMAN = ITEM_DEFERRED_REGISTER.register("nether_talisman", NetherTalisman::new);

    public static final DeferredItem<PurgatoryTalisman> PURGATORY_TALISMAN = ITEM_DEFERRED_REGISTER.register("purgatory_talisman", PurgatoryTalisman::new);

    public static final DeferredItem<HealingTalisman> HEALING_TALISMAN = ITEM_DEFERRED_REGISTER.register("healing_talisman", HealingTalisman::new);

    public static final DeferredItem<PerseveranceNecklace> PERSEVERANCE_NECKLACE = ITEM_DEFERRED_REGISTER.register("perseverance_necklace", PerseveranceNecklace::new);

    public static final DeferredItem<DragonScale> DRAGON_SCALE = ITEM_DEFERRED_REGISTER.register("dragon_scale", DragonScale::new);

    public static final DeferredItem<DarkGoldFragment> DARK_GOLD_FRAGMENT = ITEM_DEFERRED_REGISTER.register("dark_gold_fragment", DarkGoldFragment::new);

    public static final DeferredItem<DarkGold> DARK_GOLD = ITEM_DEFERRED_REGISTER.register("dark_gold", DarkGold::new);

    public static final DeferredItem<WitheringBlade> WITHERING_BLADE = ITEM_DEFERRED_REGISTER.register("withering_blade", WitheringBlade::new);

    public static final DeferredItem<Item> WITHER_SPIRIT = ITEM_DEFERRED_REGISTER.register("wither_spirit", WitherSpirit::new);

    public static final DeferredItem<Reaper> REAPER = ITEM_DEFERRED_REGISTER.register("reaper", Reaper::new);

    public static final DeferredItem<InfernoHelmet> INFERNO_HELMET = ITEM_DEFERRED_REGISTER.register("inferno_helmet", InfernoHelmet::new);

    public static final DeferredItem<InfernoChestPlate> INFERNO_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("inferno_chest_plate", InfernoChestPlate::new);

    public static final DeferredItem<InfernoLeggings> INFERNO_LEGGINGS = ITEM_DEFERRED_REGISTER.register("inferno_leggings", InfernoLeggings::new);

    public static final DeferredItem<InfernoBoots> INFERNO_BOOTS = ITEM_DEFERRED_REGISTER.register("inferno_boots", InfernoBoots::new);

    public static final DeferredItem<TwistedHelmet> TWISTED_HELMET = ITEM_DEFERRED_REGISTER.register("twisted_helmet", TwistedHelmet::new);

    public static final DeferredItem<TwistedChestPlate> TWISTED_CHEST_PLATE = ITEM_DEFERRED_REGISTER.register("twisted_chest_plate", TwistedChestPlate::new);

    public static final DeferredItem<TwistedLeggings> TWISTED_LEGGINGS = ITEM_DEFERRED_REGISTER.register("twisted_leggings", TwistedLeggings::new);

    public static final DeferredItem<TwistedBoots> TWISTED_BOOTS = ITEM_DEFERRED_REGISTER.register("twisted_boots", TwistedBoots::new);

    public static final DeferredItem<CharmOfFreshStart> CHARM_OF_FRESH_START = ITEM_DEFERRED_REGISTER.register("charm_of_fresh_start", CharmOfFreshStart::new);

    public static final DeferredItem<LastWhisper> LAST_WHISPER = ITEM_DEFERRED_REGISTER.register("last_whisper", LastWhisper::new);

    public static final DeferredItem<Item> TYRANNICAL_ESSENCE = ITEM_DEFERRED_REGISTER.register("tyrannical_essence", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    /** 臻冰：材料类物品，可叠加（沿用默认上限 64），品质史诗，带附魔光效。 */
    public static final DeferredItem<Item> ZENITH_ICE = ITEM_DEFERRED_REGISTER.register("zenith_ice", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    public static final DeferredItem<SkeletonThrowingKnife> SKELETON_THROWING_KNIFE = ITEM_DEFERRED_REGISTER.register("skeleton_throwing_knife", SkeletonThrowingKnife::new);

    public static final DeferredItem<BlockItem> EQUIPMENT_WORKBENCH = ITEM_DEFERRED_REGISTER.register("equipment_workbench", () -> new BlockItem(LRBlocks.EQUIPMENT_WORKBENCH.value(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ICE_ENCASEMENT = ITEM_DEFERRED_REGISTER.register("ice_encasement", () -> new BlockItem(LRBlocks.ICE_ENCASEMENT.value(), new Item.Properties()));

    public static final DeferredItem<ShadowDagger> SHADOW_DAGGER = ITEM_DEFERRED_REGISTER.register("shadow_dagger", ShadowDagger::new);

    public static final DeferredItem<ThunderboltNecklace> THUNDERBOLT_NECKLACE = ITEM_DEFERRED_REGISTER.register("thunderbolt_necklace", ThunderboltNecklace::new);

    /** 霜心项链：近战反伤减速 + 寒冷群系属性提升。 */
    public static final DeferredItem<FrostHeartPendant> FROSTHEART_PENDANT = ITEM_DEFERRED_REGISTER.register("frostheart_pendant", FrostHeartPendant::new);

    public static final DeferredItem<HolyCloak> HOLY_CLOAK = ITEM_DEFERRED_REGISTER.register("holy_cloak", HolyCloak::new);

    /** 雪羽斗篷：细雪免疫 + 移速提升 + 延长自己造成的减速。 */
    public static final DeferredItem<SnowFeatherCloak> SNOWFEATHER_CLOAK = ITEM_DEFERRED_REGISTER.register("snowfeather_cloak", SnowFeatherCloak::new);

    public static final DeferredItem<FrostShard> FROST_SHARD = ITEM_DEFERRED_REGISTER.register("frost_shard", FrostShard::new);

    /** 烈焰飞轮：暂无行为。 */
    public static final DeferredItem<BlazingChakram> BLAZING_CHAKRAM = ITEM_DEFERRED_REGISTER.register("blazing_chakram", BlazingChakram::new);

    /** 破冰者：暂无行为。 */
    public static final DeferredItem<Icebreaker> ICEBREAKER = ITEM_DEFERRED_REGISTER.register("icebreaker", Icebreaker::new);

    /** 霜钢剑：暂无行为。 */
    public static final DeferredItem<FrostSteelSword> FROST_STEEL_SWORD = ITEM_DEFERRED_REGISTER.register("frost_steel_sword", FrostSteelSword::new);

    /** 冰川骷髅刷怪蛋：颜色取自臻冰那套冰蓝配色。 */
    public static final DeferredItem<DeferredSpawnEggItem> GLACITE_SKELETON_SPAWN_EGG = ITEM_DEFERRED_REGISTER.register("glacite_skeleton_spawn_egg",
            () -> new DeferredSpawnEggItem(LREntityTypes.GLACITE_SKELETON, 0x82CDF0, 0x174263, new Item.Properties()));

}
