package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * EvoodooersSoundEvents 类
 * <p> 用于注册模组中的音效事件, 包括音效的定义和注册逻辑. 该类主要负责将音效资源与模组 ID 绑定, 并确保音效在模组初始化时正确加载.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersSoundEvents {
    /**
     * The provider for sound events
     */
    public static final RegistrationProvider<SoundEvent> SOUND_EVENTS = RegistrationProvider.get(Registries.SOUND_EVENT, Constants.MOD_ID);

    /** 白天音效注册项, 用于注册游戏中的白天音效资源 */
    public static final RegistryObject<SoundEvent> DAY = SOUND_EVENTS.register("record.day", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "record.day")));

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }
}
