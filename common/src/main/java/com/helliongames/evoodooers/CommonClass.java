package com.helliongames.evoodooers;

import com.helliongames.evoodooers.registration.*;

/**
 * 公共工具类
 * <p> 用于初始化游戏模组中的各类资源, 包括方块, 物品, 实体, 配方, 标签和声音事件等
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class CommonClass {
    /**
     * 初始化 Evoodooers 模组的所有资源类
     * <p> 加载模组中的各种资源类, 包括方块, 物品, 实体, 配方, 标签和声音事件等
     * @since 1.0
     */
    public static void init() {
        EvoodooersBlocks.loadClass();
        EvoodooersItems.loadClass();
        EvoodooersBlockEntities.loadClass();
        EvoodooersRecipes.loadClass();
        EvoodooersTabs.loadClass();
        EvoodooersSoundEvents.loadClass();
    }
}