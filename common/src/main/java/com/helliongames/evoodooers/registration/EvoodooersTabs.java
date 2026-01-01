package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * EvoodooersTabs 类
 * <p> 用于注册创意模式标签 (Creative Mode Tab), 主要用于在游戏内为 Evoodooers 模组创建一个自定义的物品分类标签, 方便玩家浏览和访问相关物品.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersTabs {
    /**
     * The provider for creative tabs
     */
    public static final RegistrationProvider<CreativeModeTab> TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    /** 注册 Evoodooers 模组的创意模式标签, 用于在创意菜单中展示相关物品 */
    public static final RegistryObject<CreativeModeTab> EVOODOOERS_TAB = TABS.register("evoodooers", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("itemGroup.evoodooers_tab")).icon(() -> new ItemStack(EvoodooersItems.VOODOO_DOLL.get())).displayItems((itemDisplayParameters, output) -> {
        output.accept(EvoodooersItems.VOODOO_DOLL.get());
        output.accept(EvoodooersItems.HAIR_TUFT.get());
        output.accept(EvoodooersItems.DAY_RECORD.get());
    }).build());

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }
}
