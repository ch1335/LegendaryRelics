package com.chen1335.legendaryRelics.client.renderers.curioRenderers;

import com.chen1335.legendaryRelics.client.module.curio.ShadowCloakGeoModel;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios.ShadowCloak;

/**
 * 暗影斗篷的渲染器，模型逻辑都在 {@link CloakRenderer}。
 */
public class ShadowCloakGeoRenderer extends CloakRenderer<ShadowCloak> {

    public ShadowCloakGeoRenderer() {
        super(new ShadowCloakGeoModel());
    }
}
