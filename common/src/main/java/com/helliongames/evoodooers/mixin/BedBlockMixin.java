package com.helliongames.evoodooers.mixin;

import com.helliongames.evoodooers.access.BedBlockEntityAccess;
import com.helliongames.evoodooers.container.TagEnum;
import com.helliongames.evoodooers.registration.EvoodooersItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * BedBlockMixin 混入类
 * <p> 用于扩展 BedBlock 的功能, 实现玩家睡觉时记录最后睡觉的玩家信息, 并支持使用剪刀从床上获取头发的特殊交互逻辑.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

    /**
     * 在玩家睡觉时设置最后睡觉的玩家
     * <p> 该方法在玩家睡觉时被调用, 用于记录最后睡觉的玩家信息. 仅在服务端执行, 若玩家正在睡觉, 则获取床的头部位置, 并更新对应的床块实体的最后睡觉玩家信息.
     * @param state     床块的状态
     * @param level     当前世界
     * @param pos       床块的位置
     * @param player    玩家对象
     * @param hitResult 玩家点击床块的结果
     * @param cir       回调信息, 用于返回交互结果
     */
    @Inject(
            method = "useWithoutItem",
            at = @At("TAIL")
    )
    private void evoodooers_setPlayerSlept(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide) {
            if (player.isSleeping()) {
                BlockPos headPos = state.getValue(BedBlock.PART) == BedPart.HEAD ? pos : pos.relative(state.getValue(BedBlock.FACING));
                BlockEntity be = level.getBlockEntity(headPos);
                if (be instanceof BedBlockEntityAccess access) {
                    access.evoodooers$setLastSleptPlayer(player);
                }
            }
        }
    }

    /**
     * 在床的头部使用剪刀时获取头发
     * <p> 当玩家使用剪刀在床的头部进行交互时, 从床中获取头发并给予玩家, 同时更新相关状态和播放音效.
     * @param state     床的块状态
     * @param level     当前世界
     * @param pos       床的位置
     * @param player    使用物品的玩家
     * @param hitResult 玩家点击的命中结果
     * @param cir       回调信息, 用于设置返回值
     */
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void evoodooers_getHairFromBed(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
        if (heldItem.is(Items.SHEARS) && state.getValue(BedBlock.PART) == BedPart.HEAD) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BedBlockEntityAccess access && !level.isClientSide) {
                UUID lastPlayerUUID = access.evoodooers$getLastPlayerSleptUUID();
                String playerName = access.evoodooers$getPlayerName(level);
                if (playerName != null) {
                    ItemStack hairStack = new ItemStack(EvoodooersItems.HAIR_TUFT.get());
                    CustomData.update(DataComponents.CUSTOM_DATA, hairStack, tag -> tag.putUUID(TagEnum.HAIR_OWNER_UUID.get(), lastPlayerUUID));
                    Block.popResource(level, pos, hairStack);
                    heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                    level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                    access.evoodooers$setLastSleptPlayer(null);
                    player.displayClientMessage(Component.translatable("block.evoodooers.bed.hair", playerName), true);
                } else {
                    player.displayClientMessage(Component.translatable("block.evoodooers.bed.no_hair"), true);
                    cir.setReturnValue(InteractionResult.CONSUME);
                }
            }
        }
    }
}
