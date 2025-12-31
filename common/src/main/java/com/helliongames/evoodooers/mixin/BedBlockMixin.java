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

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

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
