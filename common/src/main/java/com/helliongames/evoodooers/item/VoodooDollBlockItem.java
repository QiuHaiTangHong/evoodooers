package com.helliongames.evoodooers.item;

import com.helliongames.evoodooers.toolset.Utils;
import com.helliongames.evoodooers.container.TagEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * VoodooDollBlockItem 类
 * <p> 表示一个带有玩家名称的诅咒娃娃方块物品, 用于在物品名称中显示连接的玩家信息
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class VoodooDollBlockItem extends BlockItem {
    public VoodooDollBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    /**
     * 获取物品名称组件, 根据物品栈中的玩家名称进行翻译
     * <p> 从物品栈中获取连接玩家的 UUID, 若存在玩家名称则返回带名称的翻译组件, 否则调用父类方法获取默认名称
     * @param itemStack 物品栈, 用于获取玩家名称信息
     * @return 带玩家名称的翻译组件, 若无玩家名称则返回父类的默认名称组件
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        String playerName = Utils.fromItemStackPlayerName(itemStack, TagEnum.CONNECTED_PLAYER_UUID.get());
        if (playerName != null && !playerName.isEmpty()) {
            return Component.translatable(this.getDescriptionId() + ".named", playerName);
        }
        return super.getName(itemStack);
    }
}
