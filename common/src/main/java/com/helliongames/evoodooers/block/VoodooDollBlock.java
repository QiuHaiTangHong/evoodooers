package com.helliongames.evoodooers.block;

import com.helliongames.evoodooers.container.TagEnum;
import com.helliongames.evoodooers.entity.block.VoodooDollBlockEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VoodooDollBlock extends BaseEntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    protected static final MapCodec<VoodooDollBlock> CODEC = simpleCodec(VoodooDollBlock::new);
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public VoodooDollBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (level.getBlockEntity(pos) instanceof VoodooDollBlockEntity voodooDoll) {
            Player targetedPlayer = this.getTargetPlayer(null, level, voodooDoll);
            if (targetedPlayer == null) return;

            targetedPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, false, false, false));
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        level.setBlock(pos, state.setValue(ROTATION, RotationSegment.convertToSegment(player.getYRot() + 180.0f)), 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack heldItem, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof VoodooDollBlockEntity voodooDoll) {
            if (
                    voodooDoll.getOwnerProfile() == null &&
                            heldItem.is(Items.NAME_TAG) &&
                            heldItem.has(DataComponents.CUSTOM_NAME)
            ) {
                Component nameComponent = heldItem.get(DataComponents.CUSTOM_NAME);
                String playerName = nameComponent != null ? nameComponent.getString() : null;
                GameProfileCache gameProfileCache = player.getServer() != null ? player.getServer().getProfileCache() : null;
                if (playerName != null && !playerName.isEmpty() && gameProfileCache != null){
                    GameProfile gameProfile = player.getServer().getProfileCache().get(playerName).orElse(new GameProfile(UUIDUtil.createOfflinePlayerUUID(playerName), playerName));
                    voodooDoll.setOwner(gameProfile);
                    voodooDoll.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    player.displayClientMessage(
                            Component.translatable("message.evoodooers.bind", playerName),
                            true
                    );
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
            Player targetedPlayer = this.getTargetPlayer(player, level, voodooDoll);
            if (heldItem.is(Items.FLINT_AND_STEEL)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                targetedPlayer.igniteForSeconds(1);
                heldItem.hurtAndBreak(15, player, LivingEntity.getSlotForHand(hand));
            } else if (heldItem.is(Items.FIRE_CHARGE)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                targetedPlayer.igniteForSeconds(1);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.POWDER_SNOW_BUCKET)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                targetedPlayer.setTicksFrozen(200);
                player.setItemInHand(hand, BucketItem.getEmptySuccessItem(heldItem, player));
            } else if (heldItem.is(Items.TNT)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                if (!level.isClientSide) {
                    targetedPlayer.level().explode(null, targetedPlayer.getX() + targetedPlayer.getRandom().nextInt(2) - 1, targetedPlayer.getEyeY(), targetedPlayer.getZ() + targetedPlayer.getRandom().nextInt(2) - 1, 1.0f, false, Level.ExplosionInteraction.NONE);
                }
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.POTION) || heldItem.is(Items.SPLASH_POTION) || heldItem.is(Items.LINGERING_POTION)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                PotionContents potionContents = heldItem.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                for (MobEffectInstance effect : potionContents.getAllEffects()) {
                    targetedPlayer.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() / 10, effect.getAmplifier()));
                }
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.ECHO_SHARD)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.WARDEN_EMERGE, 5.0f);
                targetedPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160));
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.ROTTEN_FLESH)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;

                this.playSoundToPlayer(targetedPlayer, SoundEvents.ZOMBIE_AMBIENT, 1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.SPIDER_EYE)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.SPIDER_AMBIENT, 1.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.BONE)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.SKELETON_AMBIENT, 1.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.GUNPOWDER)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.CREEPER_PRIMED, 1.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.ENDER_PEARL)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.ENDERMAN_STARE, 1.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.GOLD_NUGGET)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.PIGLIN_ANGRY, 1.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.DRAGON_HEAD)) {
                if (targetedPlayer == null) return ItemInteractionResult.CONSUME;
                this.playSoundToPlayer(targetedPlayer, SoundEvents.ENDER_DRAGON_DEATH, 2.0f);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            player.displayClientMessage(
                    Component.translatable("message.evoodooers.release_curse", heldItem.getHoverName(), targetedPlayer.getName().getString()),
                    true
            );
            if (level instanceof ServerLevel serverLevel){
                this.spawnMagicCircle(serverLevel, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(heldItem, state, level, pos, player, hand, hitResult);
    }

    private void spawnMagicCircle(ServerLevel level, BlockPos pos) {
        double radius = 1;
        int particleCount = 40;
        double startY = pos.getY();
        double rotationStrength = 0.15;
        double shrinkStrength = 0.1;
        double vy = -0.125;
        for (int i = 0; i < particleCount; i++) {
            double angle = i * (2 * Math.PI / particleCount);
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;
            double vx = (-Math.cos(angle) * shrinkStrength) + (-Math.sin(angle) * rotationStrength);
            double vz = (-Math.sin(angle) * shrinkStrength) + (Math.cos(angle) * rotationStrength);
            level.sendParticles(
                    ParticleTypes.ENCHANT,
                    pos.getX() + 0.5 + xOffset,
                    startY + 0.5,
                    pos.getZ() + 0.5 + zOffset,
                    0,
                    vx,
                    vy,
                    vz,
                    5
            );
        }
    }

    private void playSoundToPlayer(Player targetedPlayer, SoundEvent sound, float volume) {
        if (targetedPlayer instanceof ServerPlayer serverPlayer) {
            float rot = serverPlayer.getYRot();
            serverPlayer.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.HOSTILE, serverPlayer.getX() + Mth.sin(rot * Mth.DEG_TO_RAD) * 2, serverPlayer.getY(), serverPlayer.getZ() - Mth.cos(rot * Mth.DEG_TO_RAD) * 2, volume, serverPlayer.getVoicePitch(), serverPlayer.getRandom().nextLong()));
        }
    }

    public Player getTargetPlayer(@Nullable Player interactingPlayer, Level level, VoodooDollBlockEntity voodooDoll) {
        if (voodooDoll.getOwnerProfile() == null) {
            if (interactingPlayer != null)
                interactingPlayer.displayClientMessage(Component.translatable("block.evoodooers.voodoo_doll.no_player"), true);
            return null;
        }

        Player targetedPlayer = null;

        if (level.getServer() == null) return null;

        for (Level serverLevel : level.getServer().getAllLevels()) {
            targetedPlayer = serverLevel.getPlayerByUUID(voodooDoll.getOwnerProfile().getId());
            if (targetedPlayer != null) break;
        }

        if (targetedPlayer == null) {
            if (interactingPlayer != null)
                interactingPlayer.displayClientMessage(Component.translatable("block.evoodooers.voodoo_doll.no_player_found", voodooDoll.getOwnerProfile().getName()), true);
            return null;
        }

        return targetedPlayer;
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        if (level.getBlockEntity(pos) instanceof VoodooDollBlockEntity voodooDollBlockEntity && voodooDollBlockEntity.getOwnerProfile() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID(TagEnum.CONNECTED_PLAYER_UUID.get(), voodooDollBlockEntity.getOwnerProfile().getId());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VoodooDollBlockEntity voodooDollBlockEntity) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.hasUUID(TagEnum.CONNECTED_PLAYER_UUID.get())) {
                    UUID uuid = tag.getUUID(TagEnum.CONNECTED_PLAYER_UUID.get());
                    GameProfile gameProfile = new GameProfile(uuid, "");
                    voodooDollBlockEntity.setOwner(gameProfile);
                }
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(blockPlaceContext.getRotation() + 180.0f));
    }

    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    public float getYRotationDegrees(BlockState blockState) {
        return RotationSegment.convertToDegrees(blockState.getValue(ROTATION));
    }

    @Override
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(ROTATION, rotation.rotate(blockState.getValue(ROTATION), 16));
    }

    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(ROTATION, mirror.mirror(blockState.getValue(ROTATION), 16));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new VoodooDollBlockEntity(blockPos, blockState);
    }
}
