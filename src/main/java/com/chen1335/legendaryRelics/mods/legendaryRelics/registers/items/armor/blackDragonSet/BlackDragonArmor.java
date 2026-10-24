package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor.BlackDragonArmorGeoModel;
import com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor.BlackDragonArmorRenderer;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.LRArmorBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * 黑龙四件套共用一套 GeckoLib 模型，部位由骨骼名区分。
 */
public class BlackDragonArmor extends LRArmorBase implements GeoItem {
    public static final ResourceLocation BlackDragonArmorTexture = LegendaryRelics.id("textures/armor/black_dragon.png");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlackDragonArmor(Type type, Properties properties) {
        super(Tier.Armor.BLACK_DRAGON, type, properties.stacksTo(1));
    }


    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return BlackDragonArmorTexture;
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.black_dragon." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 5, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        );
    }


    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

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
        return new BlackDragonArmorRenderer(new BlackDragonArmorGeoModel());
    }
}
