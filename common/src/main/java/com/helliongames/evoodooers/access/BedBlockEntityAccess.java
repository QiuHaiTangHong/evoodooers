package com.helliongames.evoodooers.access;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public interface BedBlockEntityAccess {
    void evoodooers$setLastSleptPlayer(@Nullable Player player);

    UUID evoodooers$getLastPlayerSleptUUID();

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
