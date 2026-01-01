package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.item.crafting.VoodooDollRecipe;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

/**
 * EvoodooersRecipes 类
 * <p> 用于注册与 Voodoo Doll 相关的配方序列化器, 确保配方在游戏中的正确加载和使用
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersRecipes {
    /**
     * The provider for recipes
     */
    public static final RegistrationProvider<RecipeSerializer<?>> RECIPES = RegistrationProvider.get(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    /** 注册伏都娃娃配方的序列化器 */
    public static final RegistryObject<RecipeSerializer<?>> VOODOO_DOLL_RECIPE = RECIPES.register("voodoo_doll", () -> new SimpleCraftingRecipeSerializer<>(VoodooDollRecipe::new));

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }
}
