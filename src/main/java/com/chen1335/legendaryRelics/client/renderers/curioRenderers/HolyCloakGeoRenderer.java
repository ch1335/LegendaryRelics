package com.chen1335.legendaryRelics.client.renderers.curioRenderers;

import com.chen1335.legendaryRelics.client.module.curio.HolyCloakGeoModel;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios.HolyCloak;

/**
 * 神圣斗篷的渲染器，模型逻辑都在 {@link CloakRenderer}。
 */
public class HolyCloakGeoRenderer extends CloakRenderer<HolyCloak> {

    public HolyCloakGeoRenderer() {
        super(new HolyCloakGeoModel());
    }
}
