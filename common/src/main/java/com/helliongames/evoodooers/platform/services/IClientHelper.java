package com.helliongames.evoodooers.platform.services;

/**
 * 客户端辅助接口
 * <p> 提供客户端相关资源的注册功能, 包括实体渲染器, 模型图层和渲染类型等的注册操作, 用于支持客户端的图形渲染和资源加载
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public interface IClientHelper {
    /**
     * 注册实体渲染器
     * <p> 用于注册所有实体的渲染器, 以便在游戏或应用中正确显示实体模型.
     */
    void registerEntityRenderers();

    /**
     * 注册模型层
     * <p> 用于初始化和注册应用程序中的模型层组件
     */
    void registerModelLayers();

    /**
     * 注册渲染类型
     * <p> 用于注册所有需要渲染的类型到系统中, 确保渲染系统能够识别并处理这些类型.
     */
    void registerRenderTypes();
}
