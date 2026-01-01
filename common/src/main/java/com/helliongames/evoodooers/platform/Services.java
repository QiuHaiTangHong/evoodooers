package com.helliongames.evoodooers.platform;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.platform.services.IClientHelper;
import com.helliongames.evoodooers.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.

/**
 * 服务加载类
 * <p> 提供基于 Java ServiceLoader 机制的平台服务加载功能, 用于在不同运行环境中动态加载特定接口的实现类. 支持通过 META-INF/services 文件配置具体实现类路径, 适用于模组开发中平台相关服务的动态加载.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class Services {

    // In this example we provide a platform helper which provides information about what platform the mod is running on.
    // For example this can be used to check if the code is running on Forge vs Fabric, or to ask the modloader if another
    // mod is loaded.
    /** 平台辅助接口, 用于获取模组运行的平台信息, 可用于判断运行环境 (如 Forge 或 Fabric) 以及检查其他模组是否加载 */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /** 客户端帮助器实例, 用于提供客户端相关辅助功能 */
    public static final IClientHelper CLIENT_HELPER = load(IClientHelper.class);

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // example our file on Forge points to ForgePlatformHelper while Fabric points to FabricPlatformHelper.

    /**
     * 加载指定类型的平台服务实现
     * <p> 通过 ServiceLoader 机制加载指定接口的实现类, 若加载失败则抛出异常. 该方法通常用于加载平台相关的服务实现.
     * @param clazz 要加载的服务接口类型
     * @return 加载成功的服务实现实例
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}