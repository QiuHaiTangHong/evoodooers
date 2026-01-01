package com.helliongames.evoodooers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常量类
 * <p> 用于存储和管理应用程序中常用的常量值, 包括模块 ID, 模块名称和日志记录器实例
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class Constants {

    /** 模组唯一标识符 */
    public static final String MOD_ID = "evoodooers";
    /** 模组名称, 用于标识该模组的唯一标识符 */
    public static final String MOD_NAME = "Evoodooers";
    /** 日志记录器, 用于输出系统运行时的日志信息 */
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
}