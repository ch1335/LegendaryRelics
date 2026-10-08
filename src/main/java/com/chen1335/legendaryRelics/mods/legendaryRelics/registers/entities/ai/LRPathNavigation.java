package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * 精简路径节点的地面导航。
 *
 * <p>原版 A* 产出的节点很密，而 {@code PathNavigation#tick} 每 tick 都把 MoveControl 的目标重设为
 * 「当前节点中心」。于是每换一个节点 yRot 就会被重算一次；接近节点时更糟，因为 atan2 的两个输入
 * 趋近 0，方向噪声极大，表现为身体小幅突转。这里在 trimPath 阶段把冗余节点去掉，从源头减少换点次数。
 *
 * <p>{@code Path#nodes} 是 private，所以只用公开 API：{@code truncateNodes} + {@code replaceNode}，
 * 不依赖 access transformer。
 */
public class LRPathNavigation extends GroundPathNavigation {

    /** 方向余弦阈值：夹角小于约 28° 视为同向。 */
    private static final double SAME_DIRECTION = 0.88;

    /** 允许塌缩成直线的最大高差。 */
    private static final double MAX_COLLAPSE_Y = 2.0;

    public LRPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected void trimPath() {
        super.trimPath();

        Path path = this.path;
        if (path == null) {
            return;
        }

        int next = path.getNextNodeIndex();
        int count = path.getNodeCount();
        // 至少要有「当前节点 + 终点」才算得上有中间节点可删
        if (next >= count - 1) {
            return;
        }

        Node end = path.getEndNode();
        if (end == null) {
            return;
        }

        // ① 当前节点能一眼看到终点：整条路径塌缩成一段直线
        if (this.isTraversable(path.getNode(next).asVec3(), end.asVec3())) {
            path.truncateNodes(next + 1);
            path.replaceNode(next, end);
            return;
        }

        // ② 否则合并方向几乎不变的中间节点。write 始终 <= read，所以往后压实不会覆盖未读的节点
        int write = next + 1;
        for (int read = next + 1; read < count - 1; read++) {
            Node previous = path.getNode(write - 1);
            Node current = path.getNode(read);
            Node upcoming = path.getNode(read + 1);
            if (this.isSameDirection(previous, current, upcoming)
                    && this.isTraversable(previous.asVec3(), upcoming.asVec3())) {
                continue;
            }

            path.replaceNode(write++, current);
        }

        // 终点永远保留
        path.replaceNode(write++, end);
        path.truncateNodes(write);
    }

    /** Y 加权（×3）后比较方向余弦：抬升/下落比水平位移更值得单独成节点。 */
    private boolean isSameDirection(Node a, Node b, Node c) {
        Vec3 first = b.asVec3().subtract(a.asVec3()).multiply(1.0, 3.0, 1.0);
        Vec3 second = c.asVec3().subtract(b.asVec3()).multiply(1.0, 3.0, 1.0);
        double length = first.length() * second.length();
        if (length < 1.0E-4) {
            return false;
        }

        return first.dot(second) / length > SAME_DIRECTION;
    }

    /** 两点之间是否可直线通过：不能有阻挡、不能踩到危险方块、沿途下方必须都有实心地面。 */
    private boolean isTraversable(Vec3 from, Vec3 to) {
        if (Math.abs(to.y - from.y) > MAX_COLLAPSE_Y) {
            return false;
        }

        Vec3 lift = new Vec3(0.0, 0.75, 0.0);
        ClipContext clip = new ClipContext(from.add(lift), to.add(lift),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        if (this.level.clip(clip).getType() != HitResult.Type.MISS) {
            return false;
        }

        Vec3 delta = to.subtract(from);
        double distance = delta.length();
        if (distance < 1.0E-4) {
            return true;
        }

        Vec3 step = delta.scale(1.0 / distance);
        for (int i = 0; i < (int) distance; i++) {
            BlockPos pos = BlockPos.containing(from.add(step.scale(i)));
            if (this.mob.getType().isBlockDangerous(this.level.getBlockState(pos))) {
                return false;
            }
            if (!this.level.getBlockState(pos.below()).isFaceSturdy(this.level, pos.below(), Direction.UP)) {
                return false;
            }
        }

        return true;
    }
}
