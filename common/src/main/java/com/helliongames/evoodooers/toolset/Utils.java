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

public class Utils {
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
