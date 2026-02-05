package com.chen1335.legendaryRelics.common;

import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class AttributesGetter {
    public static Holder<Attribute> healReceive() {
        return ALObjects.Attributes.HEALING_RECEIVED;
    }
}
