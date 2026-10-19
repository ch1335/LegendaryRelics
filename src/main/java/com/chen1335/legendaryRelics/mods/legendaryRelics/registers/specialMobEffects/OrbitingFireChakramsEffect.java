package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.OrbitingFireChakram;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家身上的烈焰飞轮调度效果（服务端权威）：
 * <ul>
 *     <li>命中时按生成冷却生成一个环绕飞轮（在 {@link #onAddOrUpdate} 中）。</li>
 *     <li>每 tick 重新分配各飞轮的环绕索引/总数/伤害，保证分布均匀。</li>
 *     <li>各飞轮按自己的攻击间隔轮流突进攻击。</li>
 * </ul>
 * 飞轮数量（{@link #getString()}）与剩余时间会随效果同步到客户端，用于 HUD 显示。
 * 飞轮实体列表是服务端瞬态数据，不持久化也不参与网络编码；世界重载后由后续命中重新补充。
 */
public class OrbitingFireChakramsEffect extends TimeLimitEffect {

    /** 效果存活时间（tick）；每次命中刷新，停手一段时间后过期并清理飞轮。 */
    private static final int TTL = 20 * 15;

    private float damage = 0.0F;
    private int attackInterval = 40;
    private int maxCount = 0;
    private int generationCooldown = 80;

    /** 请求本次添加/更新时尝试生成一个飞轮（瞬态，仅服务端流程使用）。 */
    private boolean generationRequested = false;
    /** 上次生成飞轮的游戏时间，用于生成冷却判定。 */
    private long lastGenerationTime = Long.MIN_VALUE;

    /** 当前存活飞轮的实体 id（服务端瞬态）。 */
    private final List<Integer> chakramIds = new ArrayList<>();
    /** HUD 显示的飞轮数量，随效果同步到客户端。 */
    private int displayCount = 0;

    public OrbitingFireChakramsEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public OrbitingFireChakramsEffect() {
        this(LRSpecialMobEffects.ORBITING_FIRE_CHAKRAMS.value());
    }

    /** 由物品在命中时调用，写入最新的数值算法结果。 */
    public void configure(float damage, int attackInterval, int maxCount, int generationCooldown) {
        this.damage = damage;
        this.attackInterval = Math.max(1, attackInterval);
        this.maxCount = Math.max(0, maxCount);
        this.generationCooldown = Math.max(1, generationCooldown);
    }

    /** 标记本次添加/更新需要尝试生成一个飞轮。 */
    public void requestGeneration() {
        this.generationRequested = true;
        initTime(TTL);
    }

    /**
     * 合并器：把新效果携带的最新参数与生成请求转移到旧实例上并返回旧实例，
     * 保留旧实例已有的飞轮列表、计时器与生成冷却状态。
     */
    public static OrbitingFireChakramsEffect merge(OrbitingFireChakramsEffect theNew, OrbitingFireChakramsEffect theOld) {
        theOld.damage = theNew.damage;
        theOld.attackInterval = theNew.attackInterval;
        theOld.maxCount = theNew.maxCount;
        theOld.generationCooldown = theNew.generationCooldown;
        if (theNew.generationRequested) {
            theOld.generationRequested = true;
        }
        theOld.initTime(TTL);
        return theOld;
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        // addEffectToEntity 后在服务端触发；此处按冷却生成飞轮
        if (livingEntity.level().isClientSide) {
            return;
        }
        if (generationRequested) {
            generationRequested = false;
            tryGenerate(livingEntity);
        }
    }

    private void tryGenerate(LivingEntity owner) {
        cleanupChakrams(owner);
        if (chakramIds.size() >= maxCount) {
            return;
        }
        long now = owner.level().getGameTime();
        if (lastGenerationTime != Long.MIN_VALUE && now - lastGenerationTime < generationCooldown) {
            return;
        }
        lastGenerationTime = now;

        OrbitingFireChakram chakram = new OrbitingFireChakram(owner.level(), owner);
        chakram.setPos(owner.getX(), owner.getY() + 1.0D, owner.getZ());
        // 用当前飞轮数量对攻击间隔取相位偏移，让新飞轮与既有飞轮错开攻击时机
        int existing = chakramIds.size();
        chakram.updateOrbit(existing, existing + 1, damage, attackInterval);
        chakram.setAttackCooldown(attackInterval + (existing * attackInterval) / Math.max(1, maxCount));
        owner.level().addFreshEntity(chakram);
        chakramIds.add(chakram.getId());
        displayCount = chakramIds.size();
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        if (livingEntity.level().isClientSide) {
            return;
        }

        cleanupChakrams(livingEntity);

        // 超出上限时移除多余飞轮
        while (chakramIds.size() > maxCount) {
            int lastIndex = chakramIds.size() - 1;
            Entity entity = livingEntity.level().getEntity(chakramIds.remove(lastIndex));
            if (entity instanceof OrbitingFireChakram chakram) {
                chakram.discard();
            }
        }

        // 重新分配环绕参数与攻击间隔；每个飞轮自行管理独立冷却并穿插攻击
        List<OrbitingFireChakram> chakrams = resolveChakrams(livingEntity);
        for (int i = 0; i < chakrams.size(); i++) {
            chakrams.get(i).updateOrbit(i, chakrams.size(), damage, attackInterval);
        }
        displayCount = chakrams.size();
    }

    /** 清理已消失/无效的飞轮 id。 */
    private void cleanupChakrams(LivingEntity owner) {
        chakramIds.removeIf(id -> {
            Entity entity = owner.level().getEntity(id);
            return !(entity instanceof OrbitingFireChakram chakram) || chakram.isRemoved();
        });
        displayCount = chakramIds.size();
    }

    private List<OrbitingFireChakram> resolveChakrams(LivingEntity owner) {
        List<OrbitingFireChakram> result = new ArrayList<>();
        for (int id : chakramIds) {
            if (owner.level().getEntity(id) instanceof OrbitingFireChakram chakram && !chakram.isRemoved()) {
                chakram.setOwner(owner);
                result.add(chakram);
            }
        }
        return result;
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide) {
            return;
        }
        for (int id : chakramIds) {
            if (livingEntity.level().getEntity(id) instanceof OrbitingFireChakram chakram) {
                chakram.discard();
            }
        }
        chakramIds.clear();
        displayCount = 0;
    }

    @Override
    public String getString() {
        return String.valueOf(displayCount);
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(displayCount);
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        displayCount = buffer.readInt();
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putFloat("Damage", damage);
        tag.putInt("AttackInterval", attackInterval);
        tag.putInt("MaxCount", maxCount);
        tag.putInt("GenerationCooldown", generationCooldown);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        damage = compoundTag.getFloat("Damage");
        attackInterval = compoundTag.getInt("AttackInterval");
        maxCount = compoundTag.getInt("MaxCount");
        generationCooldown = compoundTag.getInt("GenerationCooldown");
    }
}
