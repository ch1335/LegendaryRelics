package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class HolyCloak extends LRCuriosBase implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HolyCloak() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.HOLY_BARRIER.value().create(1, EquipmentTypes.CURIO));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 静态模型，无动画控制器
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
