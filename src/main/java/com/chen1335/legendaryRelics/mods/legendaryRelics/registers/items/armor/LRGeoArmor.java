package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor;

import com.chen1335.legendaryRelics.client.renderers.armor.LRArmorGeoModel;
import com.chen1335.legendaryRelics.client.renderers.armor.LRArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * 使用 GeckoLib 整套模型渲染的护甲基类，子类只需给出资源名。
 */
public abstract class LRGeoArmor extends LRArmorBase implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected LRGeoArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    /** 资源名，如 black_dragon_armor 对应 geo/armor/black_dragon_armor.geo.json。 */
    protected abstract String geoName();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 静态护甲，无动画
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = supplyRenderer();
                }
                return this.renderer;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private GeoArmorRenderer<?> supplyRenderer() {
        return new LRArmorRenderer<LRGeoArmor>(new LRArmorGeoModel<LRGeoArmor>(geoName()));
    }
}
