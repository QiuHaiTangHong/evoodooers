package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.block.VoodooDollBlock;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * EvoodooersBlocks 类
 * <p> 该类用于注册诅咒娃娃方块相关的数据, 包括方块的注册和加载方法. 主要用于游戏模组中, 确保方块在游戏世界中正确生成和使用.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersBlocks {

    /**
     * The provider for blocks
     */
    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MOD_ID);

    /** 注册的诅咒娃娃方块, 用于游戏中的诅咒效果 */
    public static final RegistryObject<Block> VOODOO_DOLL = BLOCKS.register("voodoo_doll", () -> new VoodooDollBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)));

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }
}
