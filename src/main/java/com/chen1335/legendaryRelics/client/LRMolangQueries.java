package com.chen1335.legendaryRelics.client;

import software.bernie.geckolib.loading.math.MolangQueries;

/**
 * 注册动画里用到的自定义 Molang 查询。
 *
 * <p>GeckoLib 没有实现 Bedrock 的 {@code query.modified_move_speed}，而未注册的查询会被当成常量 0
 * （{@code MolangQueries#getVariableFor} 用 computeIfAbsent 建了个值为 0 的变量），
 * 动画里乘它只会得到 0。这里用 {@code lr.} 前缀暴露原版的摆动幅度：0~1、且已经过平滑，等于走路动画的振幅倍率。
 */
public final class LRMolangQueries {

    /** 用本模组前缀，避免与 Bedrock 同名查询或其它模组冲突；解析器对非数字的名字一律按变量处理。 */
    public static final String MODIFIED_MOVE_SPEED = "lr.modified_move_speed";

    private LRMolangQueries() {
    }

    /** 必须在客户端初始化时调用一次；值本身每帧求值，所以注册时机不影响结果。 */
    public static void register() {
        MolangQueries.setActorVariable(MODIFIED_MOVE_SPEED,
                actor -> actor.animationState().getLimbSwingAmount());
    }
}
