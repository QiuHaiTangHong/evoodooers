package com.helliongames.evoodooers.toolset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 工具类
 * <p> 提供一些通用的辅助方法, 用于从物品堆栈中获取玩家名称信息
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class Utils {
    /**
     * 从物品堆栈中获取玩家名称
     * <p> 根据指定的 UUID 键从物品堆栈的自定义数据中获取玩家 UUID, 然后通过网络连接查找对应的玩家信息并返回玩家名称.
     * @param itemStack 物品堆栈, 用于获取自定义数据
     * @param uuidKey   自定义数据中存储玩家 UUID 的键
     * @return 玩家名称, 如果无法获取或玩家信息不存在则返回 null
     */
    @Nullable
    public static String fromItemStackPlayerName(ItemStack itemStack, String uuidKey) {
        String playerName = null;
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = customData != null ? customData.copyTag() : null;
        if (nbt != null && nbt.contains(uuidKey)) {
            UUID playerUUID = nbt.getUUID(uuidKey);
            try {
                ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
                PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(playerUUID) : null;
                if (playerInfo != null && !playerInfo.getProfile().getName().isEmpty()) {
                    playerName = playerInfo.getProfile().getName();
                }
            } catch (Exception ignored) {
            }
        }
        return playerName;
    }
}
