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

/**
 * VoodooDollRecipe 类
 * <p> 用于创建一个制作诅咒娃娃的配方, 继承自 ShapedRecipe, 定义了配方的形状和所需材料.
 * 该配方在制作时会检查玩家是否持有特定的头发碎片, 并将持有者的 UUID 添加到成品娃娃的自定义数据中.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class VoodooDollRecipe extends ShapedRecipe {

    /**
     * 初始化一个 VoodooDollRecipe 对象
     * <p> 使用指定的配方类别和配方模式创建一个 VoodooDoll 的合成配方, 该配方由头发, 小麦和骨头组成, 最终生成一个 VoodooDoll 项.
     * @param craftingBookCategory 配方所属的书籍类别
     */
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

    /**
     * 根据合成输入组装物品栈, 并处理头发装饰的拥有者 UUID
     * <p> 该方法用于合成过程中处理头发装饰的拥有者信息, 若物品栈包含指定的头发装饰, 则更新结果物品的自定义数据, 添加连接玩家的 UUID.
     * @param craftingInput 合成输入, 包含合成配方所需的所有物品
     * @param registries    注册表提供者, 用于获取注册的物品和数据
     * @return 合成后的物品栈
     */
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
