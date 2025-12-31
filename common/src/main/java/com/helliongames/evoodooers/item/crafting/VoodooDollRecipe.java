package com.helliongames.evoodooers.item.crafting;


import com.helliongames.evoodooers.container.TagEnum;
import com.helliongames.evoodooers.registration.EvoodooersItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class VoodooDollRecipe extends ShapedRecipe {

    public VoodooDollRecipe(CraftingBookCategory craftingBookCategory) {
        super(
                "",
                craftingBookCategory,
                ShapedRecipePattern.of(
                        Map.of('H', Ingredient.of(EvoodooersItems.HAIR_TUFT.get()),
                                'W', Ingredient.of(Items.WHEAT),
                                'B', Ingredient.of(Items.BONE)
                        ),
                        " H ",
                        "WBW",
                        " W "
                ),
                new ItemStack(EvoodooersItems.VOODOO_DOLL.get())
        );
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput craftingInput, HolderLookup.@NotNull Provider registries) {
        ItemStack result = super.assemble(craftingInput, registries);
        ItemStack invItem = craftingInput.getItem(1);
        if (invItem.isEmpty() || !invItem.is(EvoodooersItems.HAIR_TUFT.get())) {
            return result;
        }
        CustomData customData = invItem.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = customData != null ? customData.copyTag() : null;
        if (nbt != null && nbt.hasUUID(TagEnum.HAIR_OWNER_UUID.get())) {
            UUID owner = nbt.getUUID(TagEnum.HAIR_OWNER_UUID.get());
            result.update(
                    DataComponents.CUSTOM_DATA,
                    CustomData.EMPTY,
                    existing -> {
                        CompoundTag tempNbt = existing.copyTag();
                        tempNbt.putUUID(TagEnum.CONNECTED_PLAYER_UUID.get(), owner);
                        return CustomData.of(tempNbt);
                    }
            );
        }
        return result;
    }
}
