package com.helliongames.evoodooers.item;

import com.helliongames.evoodooers.toolset.Utils;
import com.helliongames.evoodooers.container.TagEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class VoodooDollBlockItem extends BlockItem {
    public VoodooDollBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        String playerName = Utils.fromItemStackPlayerName(itemStack, TagEnum.CONNECTED_PLAYER_UUID.get());
        if (playerName != null && !playerName.isEmpty()) {
            return Component.translatable(this.getDescriptionId() + ".named", playerName);
        }
        return super.getName(itemStack);
    }
}
