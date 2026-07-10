package com.chen1335.legendaryRelics.utils;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LRProjectileData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;
import net.neoforged.neoforgespi.Environment;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LRUtil {

    //Random ResourceLocation
    public static ResourceLocation randomLocation(int count) {
        return randomLocation(LegendaryRelics.MODID, count);
    }

    //Random ResourceLocation
    public static ResourceLocation randomLocationWithPrefix(String Prefix,int count) {
        return randomLocation(LegendaryRelics.MODID, count).withPrefix(Prefix);
    }

    public static ResourceLocation randomLocation(String nameSpace, int count) {
        Random random = new Random();

        StringBuilder word = new StringBuilder();
        for (int j = 0; j < count; j++) {
            char letter = (char) ('a' + random.nextInt(26));
            word.append(letter);
        }

        return ResourceLocation.fromNamespaceAndPath(nameSpace, word.toString());
    }

    public static LRProjectileData getProjectileData(Projectile projectile) {
        return projectile.getData(LRAttachmentTypes.PROJECTILE_DATA);
    }


    public static HitResult pick(Entity entity, double blockInteractionRange, double entityInteractionRange) {
        double d0 = Math.max(blockInteractionRange, entityInteractionRange);
        double d1 = Mth.square(d0);
        Vec3 vec3 = entity.getEyePosition();
        HitResult hitresult = entity.pick(d0, 1, false);
        double d2 = hitresult.getLocation().distanceToSqr(vec3);
        if (hitresult.getType() != HitResult.Type.MISS) {
            d1 = d2;
            d0 = Math.sqrt(d2);
        }

        Vec3 vec31 = entity.getViewVector(1);
        Vec3 vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
        AABB aabb = entity.getBoundingBox().expandTowards(vec31.scale(d0)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(
                entity, vec3, vec32, aabb, p_234237_ -> !p_234237_.isSpectator() && p_234237_.isPickable(), d1
        );
        return entityhitresult != null && entityhitresult.getLocation().distanceToSqr(vec3) < d2
                ? filterHitResult(entityhitresult, vec3, entityInteractionRange)
                : filterHitResult(hitresult, vec3, blockInteractionRange);
    }

    private static HitResult filterHitResult(HitResult hitResult, Vec3 pos, double blockInteractionRange) {
        Vec3 vec3 = hitResult.getLocation();
        if (!vec3.closerThan(pos, blockInteractionRange)) {
            Vec3 vec31 = hitResult.getLocation();
            Direction direction = Direction.getNearest(vec31.x - pos.x, vec31.y - pos.y, vec31.z - pos.z);
            return BlockHitResult.miss(vec31, direction, BlockPos.containing(vec31));
        } else {
            return hitResult;
        }
    }

    public static void splitAndAdd(List<Component> components, Component component, int w) {
        for (FormattedText formattedText : split(component, w)) {
            MutableComponent appender = Component.empty();
            formattedText.visit((style, string) -> {
                appender.append(Component.literal(string).withStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
            components.add(appender);
        }
    }

    public static List<FormattedText> split(Component component, int w) {
        if (Environment.get().getDist().isClient()) {
            return LRClient.split(component, w);
        }
        return List.of(component);
    }
}
