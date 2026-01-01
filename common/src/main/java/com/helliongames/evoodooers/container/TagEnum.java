package com.helliongames.evoodooers.container;

import com.helliongames.evoodooers.Constants;

/**
 * 标签枚举类
 * <p> 用于表示不同的标签类型, 每个标签类型对应一个唯一的字符串值, 通常用于标识特定的数据或功能模块.
 * <p> 每个枚举值包含一个字符串标识符, 通过 get() 方法返回完整的标签字符串, 格式为 "MOD_ID:identifier".
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public enum TagEnum {
    /** HAIR_OWNER_UUID 字段用于标识头发的所有者 UUID */
    HAIR_OWNER_UUID("hair_owner_uuid"),
    /** 最近睡眠的 UUID 标识 */
    LAST_SLEPT_UUID("last_slept_uuid"),
    /** 连接的玩家 UUID */
    CONNECTED_PLAYER_UUID("connected_player_uuid");
    /** 枚举值 */
    final String enumValue;

    TagEnum(String enumValue) {
        this.enumValue = enumValue;
    }

    public String get() {
        return Constants.MOD_ID + ":" + this.enumValue;
    }
}
