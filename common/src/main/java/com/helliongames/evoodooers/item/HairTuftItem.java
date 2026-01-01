package com.helliongames.evoodooers.item;

import com.helliongames.evoodooers.toolset.Utils;
import com.helliongames.evoodooers.container.TagEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 头发簇物品类
 * <p> 继承自 Item 类, 用于表示游戏中具有玩家名称显示功能的头发簇物品. 该物品在显示名称时会根据附带的 UUID 查找对应玩家名称, 并显示在物品名称中.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class HairTuftItem extends Item {

    public HairTuftItem(Properties properties) {
        super(properties);
    }

    /**
     * 获取物品的名称组件, 根据物品中的玩家名称进行翻译
     * <p> 从物品中获取玩家名称, 并使用该名称翻译组件. 如果未找到有效名称, 则调用父类方法获取默认名称.
     * @param itemStack 物品堆栈, 用于获取玩家名称
     * @return 名称组件, 包含玩家名称的翻译结果
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        String playerName = Utils.fromItemStackPlayerName(itemStack, TagEnum.HAIR_OWNER_UUID.get());
        if (playerName != null && !playerName.isEmpty()) {
            return Component.translatable(this.getDescriptionId() + ".named", playerName);
        }
        return super.getName(itemStack);
    }
}
