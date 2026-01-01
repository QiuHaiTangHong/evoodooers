package com.helliongames.evoodooers.platform;

import com.helliongames.evoodooers.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

/**
 * NeoForge 平台辅助类
 * <p> 提供与 NeoForge 平台相关的辅助功能, 包括获取平台名称, 检查模组是否加载以及判断是否为开发环境等操作
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class NeoForgePlatformHelper implements IPlatformHelper {

    /**
     * 获取平台名称
     * <p> 返回当前平台的名称, 固定为 "NeoForge"
     * @return 平台名称
     */
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    /**
     * 判断指定模组是否已加载
     * <p> 通过模组 ID 检查模组是否已被加载到当前环境中
     * @param modId 模组的唯一标识符
     * @return 如果模组已加载返回 true, 否则返回 false
     */
    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    /**
     * 判断当前是否为开发环境
     * <p> 通过检查 FMLLoader 是否处于生产环境来判断当前是否为开发环境
     * @return 如果是开发环境返回 true, 否则返回 false
     */
    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}