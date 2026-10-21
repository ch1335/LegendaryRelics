# ============================================================
# Emotecraft / PlayerAnimator 动画导出脚本（Blender 4.4+ 的 Action Slot API）
#
# 用法：在 Blender Text Editor 新建文本，粘贴本文件，改顶部参数后运行。
# 输出：.blend 同目录下的 emote.json（可用 OUTPUT_NAME 改）
#
# 相比旧版修复/改进：
#   1. action_slot 为空时不再崩溃：按 "OB<物体名>" 找槽位，找不到就跳过并打印原因
#   2. 物体不存在 / 只有 NLA 动画时给出明确提示，而不是静默丢失或 KeyError
#   3. 用 json.dump 生成，名字/描述里有引号也不会写坏 JSON
#   4. 每个部件单独容错，一个坏掉不影响其它部件导出
# ============================================================
import bpy
import json
import os
import re
from bpy_extras import anim_utils

# ---------------- 可编辑参数 ----------------
EMOTE_NAME = "name"                 # 动画 ID 的 path：<命名空间>:name
USE_ACTION_NAME = True              # True: 动画名直接取 torso 当前 action 的名字
EMOTE_DESCRIPTION = "description"
AUTHOR = "chen_1335"
IS_LOOP = False                     # 循环动画
RETURN_TICK = 2
OUTPUT_NAME = ""                    # 留空：直接用动画名（action 名）作文件名；也可写死 "emote.json"
ROUND_DIGITS = 6                    # 数值保留小数位
SKIP_TICK_ZERO = True               # 跳过第 0 帧（Emotecraft 惯例）
# --------------------------------------------

# (物体名, 是否物品部件)
BODY_PARTS = [
    ("head", False),
    ("torso", False),
    ("rightArm", False),
    ("leftArm", False),
    ("rightLeg", False),
    ("leftLeg", False),
    ("rightItem", True),
    ("leftItem", True),
]
# 这些部件不读 bend
NO_BEND_PARTS = {"head", "leftItem", "rightItem"}

# 轴映射：Blender 的 fcurve 数组下标 → MC 的通道名
# 物品部件在骨架上的轴向和身体部件不同，所以单独一张表
AXIS_MAP = {
    ("location", False): {0: "x", 1: "z", 2: "y"},
    ("location", True): {0: "x", 1: "y", 2: "z"},
    ("scale", False): {0: "scaleX", 1: "scaleZ", 2: "scaleY"},
    ("scale", True): {0: "scaleX", 1: "scaleY", 2: "scaleZ"},
    ("rotation", False): {0: "pitch", 1: "roll", 2: "yaw"},
    ("rotation", True): {0: "pitch", 1: "yaw", 2: "roll"},
}


def get_channelbag(obj):
    """取物体当前 action 的 channelbag；槽位缺失时按 "OB<物体名>" 兜底，找不到返回 None。"""
    if obj is None:
        return None
    anim = obj.animation_data
    if anim is None:
        return None
    action = anim.action
    if action is None:
        if len(anim.nla_tracks) > 0:
            print(f"[跳过] {obj.name}: 动画只在 NLA 里，请先把动作推回 action")
        return None

    slot = anim.action_slot
    if slot is None:
        wanted = "OB" + obj.name
        slot = next((s for s in action.slots if s.identifier == wanted), None)
        if slot is None:
            print(f"[跳过] {obj.name}: action '{action.name}' 里没有它的槽位")
            return None
        anim.action_slot = slot

    try:
        return anim_utils.action_get_channelbag_for_slot(action, slot)
    except Exception as exc:
        print(f"[跳过] {obj.name}: 读取 channelbag 失败（{exc}）")
        return None


def easing_of(keyframe):
    """Blender 的 easing/interpolation → Emotecraft 的 easing 字符串。"""
    if keyframe.easing == "AUTO":
        easing = "EASEINOUT"
    else:
        easing = "".join(keyframe.easing.split("_"))
    if keyframe.interpolation == "BEZIER":
        easing += "QUAD"
    elif keyframe.interpolation in ("CONSTANT", "LINEAR"):
        easing = keyframe.interpolation
    else:
        easing += str(keyframe.interpolation)
    return easing


def convert_value(name, axis, value, is_location, is_item):
    """Blender 单位/方向 → MC 模型单位/弧度。"""
    # 头部位置基准补偿
    if is_location and name == "head" and axis == "y":
        value -= 3

    if is_location:
        if name != "torso":
            value *= 4
            if not is_item:
                value *= -1
        else:
            value *= 0.25
            if axis == "z":
                value *= -1
    elif is_item:
        pass
    elif not (name == "torso" and axis not in ("roll", "bend")) and "scale" not in axis:
        # 旋转方向整体取反，躯干的 roll/bend 和缩放除外
        value *= -1

    # 基准点补偿（腿、手臂的 pivot 与 MC 不同）
    if axis == "y" and name in ("rightLeg", "leftLeg"):
        value += 12
    if axis in ("x", "z"):
        if name == "rightLeg":
            value += 0.1
        elif name == "leftLeg":
            value -= 0.1
    if axis == "y" and name in ("rightArm", "leftArm"):
        value += 12

    return value


def make_move(part_name, axis, tick, keyframe, is_location, is_item):
    value = convert_value(part_name, axis, keyframe.co[1], is_location, is_item)
    return {
        "tick": tick,
        "easing": easing_of(keyframe),
        "turn": 0,
        part_name: {axis: round(value, ROUND_DIGITS)},
    }


def collect_part(part_name, is_item, end_tick):
    """导出某个部件的 location / rotation / scale 关键帧。"""
    obj = bpy.data.objects.get(part_name)
    if obj is None:
        print(f"[跳过] 找不到物体 {part_name}")
        return []

    bag = get_channelbag(obj)
    if bag is None:
        return []

    entries = []
    for fcurve in bag.fcurves:
        if fcurve.data_path == "rotation_quaternion":
            continue  # 只支持欧拉旋转
        if fcurve.data_path == "location":
            kind, is_location = "location", True
        elif fcurve.data_path == "scale":
            kind, is_location = "scale", False
        elif fcurve.data_path.startswith("rotation"):
            kind, is_location = "rotation", False
        else:
            continue

        axis = AXIS_MAP[(kind, is_item)].get(fcurve.array_index)
        if axis is None:
            continue

        for keyframe in fcurve.keyframe_points:
            tick = int(keyframe.co[0])
            if SKIP_TICK_ZERO and tick == 0:
                continue
            if tick > end_tick[0]:
                end_tick[0] = tick
            entries.append(make_move(part_name, axis, tick, keyframe, is_location, is_item))

    if entries:
        print(f"  {part_name}: {len(entries)} 个关键帧")
    return entries


def collect_bend(part_name, end_tick):
    """导出 <部件>_bend 物体的第一个欧拉轴作为 bend。"""
    if part_name in NO_BEND_PARTS:
        return []

    bag = get_channelbag(bpy.data.objects.get(part_name + "_bend"))
    if bag is None:
        return []

    entries = []
    for fcurve in bag.fcurves:
        if fcurve.data_path != "rotation_euler" or fcurve.array_index != 0:
            continue
        for keyframe in fcurve.keyframe_points:
            tick = int(keyframe.co[0])
            if SKIP_TICK_ZERO and tick == 0:
                continue
            if tick > end_tick[0]:
                end_tick[0] = tick
            value = convert_value(part_name, "bend", keyframe.co[1], False, False)
            entries.append({
                "tick": tick,
                "easing": easing_of(keyframe),
                "turn": 0,
                part_name: {"bend": round(value, ROUND_DIGITS)},
            })

    if entries:
        print(f"  {part_name}(bend): {len(entries)} 个关键帧")
    return entries


def resolve_emote_name():
    """USE_ACTION_NAME 开启时取 torso 的 action 名；torso 没动画就退回其它部件，再退回手填值。"""
    if not USE_ACTION_NAME:
        return EMOTE_NAME
    for part in ("torso", "head", "rightArm", "leftArm", "rightLeg", "leftLeg"):
        obj = bpy.data.objects.get(part)
        if obj is not None and obj.animation_data is not None and obj.animation_data.action is not None:
            return obj.animation_data.action.name
    return EMOTE_NAME


def main():
    if bpy.data.filepath == "":
        raise SystemExit("[错误] 先保存 .blend，脚本要写到它同目录")

    raw_name = resolve_emote_name()
    # 动画 ID 的 path 只允许小写字母/数字/下划线/点/斜杠/横杠
    emote_name = re.sub(r"[^a-z0-9_./-]", "_", raw_name.lower())
    if emote_name != raw_name:
        print(f"[提示] 动画名做了合法化：{raw_name!r} → {emote_name!r}")

    scene = bpy.context.scene
    end_tick = [0]
    moves = []

    print("=== 导出 emote ===")
    for name, is_item in BODY_PARTS:
        moves.extend(collect_part(name, is_item, end_tick))
        moves.extend(collect_bend(name, end_tick))

    if not moves:
        raise SystemExit("[错误] 没有导出任何关键帧：检查物体名、Action Slot 和 NLA")

    emote = {
        "name": emote_name,
        "author": AUTHOR,
        "description": EMOTE_DESCRIPTION,
        "emote": {
            "isLoop": IS_LOOP,
            "returnTick": int(RETURN_TICK),
            "beginTick": int(scene.frame_start),
            "endTick": end_tick[0],
            "stopTick": int(scene.frame_end) + 1,
            "degrees": False,
            "moves": moves,
        },
    }

    out_name = OUTPUT_NAME if OUTPUT_NAME else f"{emote_name}.json"
    out_path = os.path.join(os.path.dirname(bpy.data.filepath), out_name)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(emote, f, ensure_ascii=False, indent=2)

    print(f"=== 完成：{len(moves)} 条关键帧 → {out_path}")
    print(f"    动画名={emote_name}  endTick={end_tick[0]}  stopTick={int(scene.frame_end) + 1}")


main()
