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
 * 修改床的逻辑，让床可以保存自定义的Nbt数据.
 */
@Mixin(BedBlockEntity.class)
public abstract class BedBlockEntityMixin extends BlockEntity implements BedBlockEntityAccess {
    @Unique
    private UUID evoodooers$lastSleptPlayerUUID = null;

    public BedBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.evoodooers$lastSleptPlayerUUID != null) {
            tag.putUUID(TagEnum.LAST_SLEPT_UUID.get(), this.evoodooers$lastSleptPlayerUUID);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TagEnum.LAST_SLEPT_UUID.get())) {
            this.evoodooers$lastSleptPlayerUUID = tag.getUUID(TagEnum.LAST_SLEPT_UUID.get());
        }
    }

    @Override
    public void evoodooers$setLastSleptPlayer(@Nullable Player player) {
        if (player != null) {
            this.evoodooers$lastSleptPlayerUUID = player.getUUID();
        } else {
            this.evoodooers$lastSleptPlayerUUID = null;
        }
    }

    @Override
    public UUID evoodooers$getLastPlayerSleptUUID() {
        return this.evoodooers$lastSleptPlayerUUID;
    }
}
