package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.animation.KeyframeAction;
import com.chen1335.legendaryRelics.animation.PlayerAnimationState;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.PlayerAnimationHandler;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.misc.IceThorn;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.arcanumlib.util.SimpleSchedule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

/**
 * 破冰者：右键播放挥砍动画。
 */
public class Icebreaker extends LRMeleeWeaponBase {
    /**
     * 挥砍动画 id（= player_animations 里动画的 name，不是文件名）。
     */
    public static final ResourceLocation SWING_ANIMATION = LegendaryRelics.id("icebreaker_swing");
    /**
     * 右键间隔（秒），同时用作物品冷却：略长于动画时长（emote.json 61 tick ≈ 3.05s），避免连点把收招截断。
     */
    @Calculator
    public static final FinalCalculator SWING_COOLDOWN = FinalCalculator.of(Constant.of(3.1F));

    private static final int RING_INTERVAL = 3;
    /**
     * 冰刺波中心相对玩家前移的距离（格）。
     */
    private static final float WAVE_OFFSET = 1.0F;
    /**
     * 单根冰刺的伤害：7 + 玩家攻击力。
     */
    @Calculator
    public static final FinalCalculator ICE_WAVE_DAMAGE = FinalCalculator.of(Add.of(
            Constant.of(7),
            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
    ));

    public Icebreaker() {
        super(Tier.Weapon.ICEBREAKER, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 冷却中直接吞掉这次右键：客户端返回 pass 就不会给服务端发包，省得按住右键刷包
        if (player.getCooldowns().isOnCooldown(this) || hand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        int cooldown = Math.max(1, Math.round(SWING_COOLDOWN.getValue(CalculatorArg.simpleArg(player, stack)) * 20.0F));
        player.getCooldowns().addCooldown(this, cooldown);
        PlayerAnimationHandler.play(player, SWING_ANIMATION, 2);
        LRPlayerData.getData(player).playerAnimationState = PlayerAnimationState.start(22)
                .checker(() -> player.getMainHandItem().is(this))
                .addKeyframeAction(15, new KeyframeAction.Run(state -> {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.MACE_SMASH_GROUND, SoundSource.PLAYERS, 1, 1);
                        doIceWaves(player, stack);
                    }
                }));
        // 客户端也必须返回成功，原版才会把这次右键发给服务端
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg arg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            boolean shift = tooltipFlag.hasShiftDown();
            // 物理伤害由 ComponentHolders 统一拼上"物理伤害"字样，所以语言文件里只留 %s
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.icebreaker.desc.1",
                            ComponentHolders.Combat.physicalDamage(ICE_WAVE_DAMAGE.toComponent(shift, arg))).withColor(0xaeaeae),
                    maxToolTipWidth(stack));
            // 补充说明用更暗的颜色，与收割者、霜锥的背景行一致
            LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.icebreaker.desc.2",
                    (int) (IceThorn.Wave.REPEAT_MULTIPLIER * 100)).withColor(5592405), maxToolTipWidth(stack));
        }
    }

    /**
     * 三圈冰刺从内到外依次生成；中心在施放瞬间锁定（朝视角水平方向前移一格），后续环不跟随玩家移动。
     */
    public void doIceWaves(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        float damage = ICE_WAVE_DAMAGE.getValue(CalculatorArg.simpleArg(player, stack));
        Vec3 center = player.position().add(horizontalLook(player).scale(WAVE_OFFSET));
        IceThorn.Wave wave = new IceThorn.Wave();
        for (int ring = 0; ring < 3; ring++) {
            final int ringIndex = ring;
            SimpleSchedule.addSchedule(level, new SimpleSchedule.Wait(
                    () -> spawnIceRing(level, player, center, ringIndex, damage, wave),
                    ringIndex * RING_INTERVAL));
        }
    }

    private static void spawnIceRing(ServerLevel level, Player owner, Vec3 center, int ringIndex, float damage, IceThorn.Wave wave) {
        float radius = ringIndex + 1;
        int count = (ringIndex + 1) * 8;
        // 隔圈错开半个间距，避免相邻环的冰刺排成一条直线
        double angleOffset = ringIndex % 2 == 0 ? 0.0 : Math.PI / count;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0 * i / count + angleOffset;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = findGroundY(level, x, center.y, z);
            IceThorn.spawn(level, x, y, z, owner.isRemoved() ? null : owner, damage, wave);
        }
    }

    /**
     * 玩家视角的水平方向单位向量；垂直俯仰时返回零向量，避免 normalize 出 NaN。
     */
    private static Vec3 horizontalLook(Player player) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        return horizontal.lengthSqr() < 1.0E-4 ? Vec3.ZERO : horizontal.normalize();
    }

    /**
     * 从中心高度往上找一块可站立的方块，找不到就用中心高度兜底。
     */
    private static double findGroundY(ServerLevel level, double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(y) + 2, Mth.floor(z));
        for (int i = 0; i < 5; i++) {
            BlockPos ground = pos.immutable();
            BlockState state = level.getBlockState(ground);
            if (state.isFaceSturdy(level, ground, Direction.UP) && level.getBlockState(ground.above()).isAir()) {
                return ground.getY() + 1.0;
            }
            pos.move(Direction.DOWN);
        }
        return y;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return false;
    }
}
