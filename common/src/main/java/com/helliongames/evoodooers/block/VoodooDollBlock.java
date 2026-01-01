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

/**
 * VoodooDollBlock 类
 * <p> 该类代表一个魔法娃娃方块, 用于在游戏中实现与玩家互动的特殊功能. 该方块可以与玩家进行交互, 例如绑定玩家, 施加效果, 播放音效等. 它还支持旋转和动画效果, 用于增强游戏体验.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class VoodooDollBlock extends BaseEntityBlock {
    /** 旋转属性, 用于表示方块的旋转状态 */
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    /** 用于解码 VoodooDollBlock 的编码器 */
    protected static final MapCodec<VoodooDollBlock> CODEC = simpleCodec(VoodooDollBlock::new);
    /** 表示方块的碰撞形状, 定义了方块在游戏世界中的物理交互范围 */
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public VoodooDollBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    /**
     * 返回该方块的编码器配置
     * <p> 用于注册方块的编码器, 返回预定义的 CODEC 配置
     * @return 方块的编码器配置
     */
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 当实体踩踏到该方块时触发的逻辑
     * <p> 检查该方块是否有 VoodooDollBlockEntity, 如果有则获取目标玩家并施加减速效果
     * @param level  当前世界
     * @param pos    方块位置
     * @param state  方块状态
     * @param entity 触发踩踏的实体
     */
    @Override
    public void stepOn(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (level.getBlockEntity(pos) instanceof VoodooDollBlockEntity voodooDoll) {
            Player targetedPlayer = this.getTargetPlayer(null, level, voodooDoll);
            if (targetedPlayer == null) return;

            targetedPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, false, false, false));
        }
    }

    /**
     * 在没有手持物品的情况下使用方块
     * <p> 该方法用于处理玩家在没有手持物品时对方块的交互行为, 会根据玩家的旋转角度调整方块的旋转状态, 并返回成功交互的结果.
     * @param state     方块当前的状态
     * @param level     游戏世界对象
     * @param pos       方块所在的位置
     * @param player    与方块交互的玩家
     * @param hitResult 玩家与方块的碰撞结果
     * @return 交互结果, 表示交互是否成功
     */
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        level.setBlock(pos, state.setValue(ROTATION, RotationSegment.convertToSegment(player.getYRot() + 180.0f)), 1);
        return InteractionResult.SUCCESS;
    }

    /**
     * 处理物品在方块上的使用交互逻辑
     * <p>该方法用于处理玩家使用物品对特定方块 (如灵魂娃娃) 进行交互时的行为逻辑, 包括绑定玩家, 施加效果, 触发爆炸等操作.
     * @param heldItem  玩家手持的物品
     * @param state     当前方块的状态
     * @param level     当前世界层级
     * @param pos       方块的位置
     * @param player    使用物品的玩家
     * @param hand      使用物品的手
     * @param hitResult 玩家点击方块的命中结果
     * @return 交互结果, 表示该交互是否成功, 消耗物品等
     */
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

    /**
     * 在指定位置生成魔法圆圈效果, 用于视觉展示
     * <p> 通过循环计算粒子位置和速度, 向指定位置发送魔法粒子效果, 形成旋转收缩的视觉效果
     * @param level 世界层级, 用于发送粒子效果
     * @param pos   魔法圆圈生成的中心坐标
     */
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

    /**
     * 向指定玩家播放音效
     * <p> 根据目标玩家的位置和方向计算音效播放位置, 并通过网络发送音效数据给客户端
     * @param targetedPlayer 目标玩家
     * @param sound          音效事件
     * @param volume         音量
     */
    private void playSoundToPlayer(Player targetedPlayer, SoundEvent sound, float volume) {
        if (targetedPlayer instanceof ServerPlayer serverPlayer) {
            float rot = serverPlayer.getYRot();
            serverPlayer.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.HOSTILE, serverPlayer.getX() + Mth.sin(rot * Mth.DEG_TO_RAD) * 2, serverPlayer.getY(), serverPlayer.getZ() - Mth.cos(rot * Mth.DEG_TO_RAD) * 2, volume, serverPlayer.getVoicePitch(), serverPlayer.getRandom().nextLong()));
        }
    }

    /**
     * 根据傀儡的所有者 ID 查找对应玩家
     * <p> 首先检查傀儡是否有所有者, 若没有则返回 null. 若存在所有者, 则遍历服务器中的所有世界, 查找对应 UUID 的玩家, 若找到则返回该玩家, 否则返回 null.
     * @param interactingPlayer 与傀儡交互的玩家, 用于发送提示信息
     * @param level             当前世界对象
     * @param voodooDoll        傀儡实体对象
     * @return 找到的玩家对象, 若未找到则返回 null
     */
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

    /**
     * 获取克隆物品堆栈
     * <p> 根据给定的区块位置和状态获取克隆物品堆栈, 若该区块包含附魔娃娃方块实体且拥有所有者资料, 则将所有者 UUID 写入物品自定义数据中.
     * @param level 区块所在的世界
     * @param pos   区块位置
     * @param state 区块状态
     * @return 克隆物品堆栈
     */
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

    /**
     * 设置方块被放置时的相关信息, 包括连接玩家的 UUID
     * <p> 该方法在方块被放置时调用, 用于设置 VoodooDoll 方块实体的拥有者信息, 若物品有自定义数据且包含连接玩家的 UUID, 则将其设置为方块实体的拥有者.
     * @param level  世界对象
     * @param pos    方块位置
     * @param state  方块状态
     * @param entity 放置方块的实体 (可能为 null)
     * @param stack  放置方块使用的物品堆栈
     */
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

    /**
     * 获取方块的渲染形状
     * <p> 返回该方块的渲染形状, 用于控制方块在游戏中的显示方式.
     * @param blockState 方块状态, 用于确定渲染形状
     * @return 渲染形状, 不会为 null
     */
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /**
     * 根据放置上下文获取块状态
     * <p> 根据给定的放置上下文, 返回调整旋转后的块状态, 将旋转角度增加 180 度后转换为段值
     * @param blockPlaceContext 块放置上下文, 包含放置位置和方向等信息
     * @return 调整旋转后的块状态
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(blockPlaceContext.getRotation() + 180.0f));
    }

    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    /**
     * 定义方块状态的属性
     * <p> 用于向状态定义器添加旋转属性, 以支持方块的旋转状态
     * @param builder 状态定义构建器, 用于构建方块状态的定义
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    /**
     * 获取方块状态的 Y 轴旋转角度 (以度为单位)
     * <p> 将方块状态中的旋转值转换为对应的度数表示
     * @param blockState 方块状态对象
     * @return Y 轴旋转角度 (以度为单位)
     */
    public float getYRotationDegrees(BlockState blockState) {
        return RotationSegment.convertToDegrees(blockState.getValue(ROTATION));
    }

    /**
     * 根据旋转方向旋转方块状态
     * <p> 该方法用于根据指定的旋转方向对方块状态进行旋转操作, 更新其旋转属性值.
     * @param blockState 当前方块状态
     * @param rotation   旋转方向
     * @return 旋转后的方块状态
     */
    @Override
    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(ROTATION, rotation.rotate(blockState.getValue(ROTATION), 16));
    }

    /**
     * 对给定的方块状态进行镜像操作
     * <p> 根据指定的镜像方式对方块状态的旋转值进行镜像处理, 返回新的方块状态
     * @param blockState 要镜像的方块状态
     * @param mirror     镜像方式
     * @return 镜像处理后的方块状态
     */
    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(ROTATION, mirror.mirror(blockState.getValue(ROTATION), 16));
    }

    /**
     * 创建一个新的方块实体
     * <p> 根据给定的坐标和方块状态生成并返回一个 VoodooDollBlockEntity 实例.
     * @param blockPos   方块坐标
     * @param blockState 方块状态
     * @return 新创建的方块实体, 可能为 null
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new VoodooDollBlockEntity(blockPos, blockState);
    }
}
