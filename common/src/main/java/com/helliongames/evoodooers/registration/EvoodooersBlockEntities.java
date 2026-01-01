package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.entity.block.VoodooDollBlockEntity;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * EvoodooersBlockEntities 类
 * <p> 用于注册和管理模组中的方块实体类型, 主要负责方块实体的注册和加载逻辑
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersBlockEntities {
    /**
     * The provider for block entities
     */
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }

    /** 注册诅咒娃娃方块实体类型, 用于游戏内方块实体的创建和管理 */
    public static final RegistryObject<BlockEntityType<VoodooDollBlockEntity>> VOODOO_DOLL = BLOCK_ENTITIES.register("voodoo_doll", () -> BlockEntityType.Builder.of(VoodooDollBlockEntity::new, EvoodooersBlocks.VOODOO_DOLL.get()).build(null));


}
