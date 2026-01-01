package com.helliongames.evoodooers;

import com.helliongames.evoodooers.client.ClientClass;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Evoodooers 模组主类
 * <p> 负责初始化模组相关功能, 注册事件监听器并执行客户端初始化逻辑
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mod(Constants.MOD_ID)
public class Evoodooers {

    /**
     * 初始化 Evoodoo 模组相关功能
     * <p> 注册事件监听器并初始化公共类
     * @param modEventBus  模组事件总线, 用于注册事件监听
     * @param modContainer 模组容器, 提供模组相关信息
     */
    public Evoodooers(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        CommonClass.init();
    }

    /**
     * 初始化客户端设置
     * <p> 在 FML 客户端设置事件中执行初始化工作, 调用 ClientClass 的 init 方法进行客户端相关初始化操作.
     * @param event FML 客户端设置事件, 用于触发初始化工作
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientClass::init);
    }
}