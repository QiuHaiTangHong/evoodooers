package com.helliongames.evoodooers.client;

import com.helliongames.evoodooers.platform.Services;

/**
 * 客户端类
 * <p> 用于初始化客户端相关的功能模块, 包括模型图层注册和实体渲染器注册等操作
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class ClientClass {
    /**
     * 初始化客户端相关服务
     * <p> 注册模型图层和实体渲染器, 用于客户端功能的初始化配置
     * @since 1.0
     */
    public static void init() {
        Services.CLIENT_HELPER.registerModelLayers();
        Services.CLIENT_HELPER.registerEntityRenderers();
    }
}
