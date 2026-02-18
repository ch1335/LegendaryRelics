package com.chen1335.legendaryRelics.common;

import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class AttributesGetter {
    public static Holder<Attribute> healReceive() {
        return ALObjects.Attributes.HEALING_RECEIVED;
    }

    public static Holder<Attribute> drawSpeed() {
        return ALObjects.Attributes.DRAW_SPEED;
    }

    public static Holder<Attribute> projectDamage() {
        return ALObjects.Attributes.PROJECTILE_DAMAGE;
    }

    public static Holder<Attribute> arrowDamage() {
        return ALObjects.Attributes.ARROW_DAMAGE;
    }

    public static Holder<Attribute> armorPierce() {
        return ALObjects.Attributes.ARMOR_PIERCE;
    }

    public static Holder<Attribute> armorShred() {
        return ALObjects.Attributes.ARMOR_SHRED;
    }

    public static Holder<Attribute> arrowVelocity() {
        return ALObjects.Attributes.ARROW_VELOCITY;
    }

    public static Holder<Attribute> lifeSteal() {
        return ALObjects.Attributes.LIFE_STEAL;
    }

    public static Holder<Attribute> miningSpeed() {
        return ALObjects.Attributes.MINING_SPEED;
    }

    public static Holder<Attribute> critDamame() {
        return ALObjects.Attributes.CRIT_DAMAGE;
    }

    public static Holder<Attribute> critChance() {
        return ALObjects.Attributes.CRIT_CHANCE;
    }
}
