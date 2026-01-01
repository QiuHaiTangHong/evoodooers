package com.helliongames.evoodooers.mixin;

import com.helliongames.evoodooers.access.BedBlockEntityAccess;
import com.helliongames.evoodooers.container.TagEnum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 床块实体混合类
 * <p> 用于扩展床块实体的功能, 主要提供玩家最后一次睡觉的 UUID 存储与读取逻辑, 支持在数据保存和加载过程中处理该信息.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mixin(BedBlockEntity.class)
public abstract class BedBlockEntityMixin extends BlockEntity implements BedBlockEntityAccess {
    /** 最近睡眠的玩家 UUID */
    @Unique
    private UUID evoodooers$lastSleptPlayerUUID = null;

    public BedBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 保存附加数据到 NBT 标签中
     * <p> 该方法用于保存额外的数据到给定的 NBT 标签中, 包括最后睡眠的玩家 UUID.
     * @param tag        要保存数据的 NBT 标签
     * @param registries 注册表提供者, 用于获取注册表信息
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.evoodooers$lastSleptPlayerUUID != null) {
            tag.putUUID(TagEnum.LAST_SLEPT_UUID.get(), this.evoodooers$lastSleptPlayerUUID);
        }
    }

    /**
     * 加载额外的数据到实体中
     * <p> 从给定的 NBT 标签中加载额外的玩家 UUID 信息, 用于记录最后睡觉的玩家
     * @param tag        包含额外数据的 NBT 标签
     * @param registries 注册表提供者, 用于查找注册的资源
     */
    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TagEnum.LAST_SLEPT_UUID.get())) {
            this.evoodooers$lastSleptPlayerUUID = tag.getUUID(TagEnum.LAST_SLEPT_UUID.get());
        }
    }

    /**
     * 设置最后被玩家睡觉的玩家
     * <p> 将指定玩家设置为最后被睡觉的玩家, 若玩家为空则将对应字段设为 null
     * @param player 被睡觉的玩家, 可为 null
     */
    @Override
    public void evoodooers$setLastSleptPlayer(@Nullable Player player) {
        if (player != null) {
            this.evoodooers$lastSleptPlayerUUID = player.getUUID();
        } else {
            this.evoodooers$lastSleptPlayerUUID = null;
        }
    }

    /**
     * 获取最后睡眠的玩家 UUID
     * <p> 返回最近一次睡眠操作所关联的玩家 UUID, 若未发生过睡眠操作则可能返回空值或默认值
     * @return 最后睡眠的玩家 UUID
     */
    @Override
    public UUID evoodooers$getLastPlayerSleptUUID() {
        return this.evoodooers$lastSleptPlayerUUID;
    }
}
