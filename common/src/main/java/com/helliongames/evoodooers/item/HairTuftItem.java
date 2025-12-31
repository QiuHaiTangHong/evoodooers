package com.helliongames.evoodooers.item;

import com.helliongames.evoodooers.toolset.Utils;
import com.helliongames.evoodooers.container.TagEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HairTuftItem extends Item {

    public HairTuftItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        String playerName = Utils.fromItemStackPlayerName(itemStack, TagEnum.HAIR_OWNER_UUID.get());
        if (playerName != null && !playerName.isEmpty()) {
            return Component.translatable(this.getDescriptionId() + ".named", playerName);
        }
        return super.getName(itemStack);
    }
}
