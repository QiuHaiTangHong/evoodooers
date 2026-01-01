package com.helliongames.evoodooers.access;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * BedBlockEntityAccess 接口
 * <p> 用于访问与床方块实体相关的玩家睡眠信息, 提供设置最后睡眠玩家, 获取最后睡眠玩家 UUID 以及根据世界获取玩家名称的功能.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public interface BedBlockEntityAccess {
    /**
     * 设置最后睡眠的玩家
     * <p> 用于更新最后睡眠的玩家信息, 该方法接受一个可为空的玩家对象作为参数
     * @param player 要设置为最后睡眠的玩家, 可以为 null
     */
    void evoodooers$setLastSleptPlayer(@Nullable Player player);

    /**
     * 获取最后一位睡眠玩家的 UUID
     * <p> 返回最近一次睡眠操作的玩家对应的唯一标识符
     * @return 最后一位睡眠玩家的 UUID, 若无睡眠记录则返回 null
     */
    UUID evoodooers$getLastPlayerSleptUUID();

    /**
     * 获取玩家名称
     * <p> 根据给定的 Level 对象获取最后睡眠的玩家名称, 若玩家不存在则返回 null.
     * @param level Level 对象, 用于获取服务器和玩家信息
     * @return 玩家名称, 若玩家不存在则返回 null
     */
    default String evoodooers$getPlayerName(Level level) {
        if (level.getServer() != null && !level.isClientSide) {
            UUID lastPlayerUUID = this.evoodooers$getLastPlayerSleptUUID();
            if (lastPlayerUUID != null) {
                ServerPlayer target = level.getServer().getPlayerList().getPlayer(lastPlayerUUID);
                String playerName = null;
                if (target != null) {
                    playerName = target.getName().getString();
                } else if (level.getServer().getProfileCache() != null) {
                    playerName = level
                            .getServer()
                            .getProfileCache()
                            .get(lastPlayerUUID)
                            .map(GameProfile::getName)
                            .orElse(null);
                }
                return playerName;
            }
        }
        return null;
    }
}
