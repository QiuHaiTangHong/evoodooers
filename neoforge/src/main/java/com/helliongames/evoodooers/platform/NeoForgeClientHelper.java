package com.helliongames.evoodooers.platform;

import com.helliongames.evoodooers.client.render.VoodooDollRenderer;
import com.helliongames.evoodooers.platform.services.IClientHelper;
import com.helliongames.evoodooers.registration.EvoodooersBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 客户端辅助类
 * <p> 提供客户端相关的注册和初始化功能, 用于处理实体渲染器, 模型层和渲染类型等 Minecraft 客户端相关操作
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@EventBusSubscriber(modid = "evoodooers", value = Dist.CLIENT)
public class NeoForgeClientHelper implements IClientHelper {
    /**
     * 注册实体渲染层定义监听器
     * <p> 用于在实体渲染器事件中注册模型层定义, 通常用于自定义实体的渲染效果
     * @param event 实体渲染器事件, 包含注册层定义的方法
     */
    @SubscribeEvent
    public static void registerModelLayerListener(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    /**
     * 注册实体渲染器监听器
     * <p> 用于在实体渲染器注册事件中注册傀儡娃娃的方块实体渲染器
     * @param event 实体渲染器注册事件
     */
    @SubscribeEvent
    public static void registerEntityRendererListener(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EvoodooersBlockEntities.VOODOO_DOLL.get(), VoodooDollRenderer::new);
    }

    /**
     * 注册实体渲染器
     * <p> 用于注册游戏中的实体渲染器, 通常在初始化阶段调用以设置实体的视觉表现方式.
     * @since 1.0
     */
    @Override
    public void registerEntityRenderers() {
    }

    /**
     * 注册模型层
     * <p> 用于注册模型相关的层, 具体实现由子类定义.
     * @since 1.0
     */
    @Override
    public void registerModelLayers() {
    }

    /**
     * 注册渲染类型
     * <p> 用于注册自定义的渲染类型, 通常在初始化阶段调用以支持特定的渲染功能.
     * @since 1.0
     */
    @Override
    public void registerRenderTypes() {
    }
}
