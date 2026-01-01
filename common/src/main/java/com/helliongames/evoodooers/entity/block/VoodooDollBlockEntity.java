package com.helliongames.evoodooers.entity.block;

import com.helliongames.evoodooers.container.TagEnum;
import com.helliongames.evoodooers.registration.EvoodooersBlockEntities;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static com.helliongames.evoodooers.Constants.LOG;

/**
 * VoodooDollBlockEntity 类
 * <p> 表示一个带有连接玩家 UUID 的方块实体, 用于存储和更新与玩家相关的游戏资料信息. 该类主要处理与游戏资料相关的数据保存, 加载以及更新逻辑, 支持在客户端和服务器端同步玩家信息.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class VoodooDollBlockEntity extends BlockEntity {
    /** 游戏用户资料缓存, 用于存储和快速访问游戏用户的资料信息 */
    @Nullable
    private static GameProfileCache profileCache;
    /** Minecraft 会话服务实例, 用于处理玩家会话相关操作 */
    @Nullable
    private static MinecraftSessionService sessionService;
    /** 主线程执行器, 用于在主线程执行任务 */
    @Nullable
    private static Executor mainThreadExecutor;
    /** 拥有者游戏资料 */
    @Nullable
    private GameProfile owner;

    /**
     * 创建一个 VoodooDollBlockEntity 实例
     * <p> 初始化 VoodooDollBlockEntity 对象, 指定其类型, 位置和状态
     * @param type  实体类型
     * @param pos   实体在世界中的位置
     * @param state 实体所在的方块状态
     */
    public VoodooDollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 初始化一个 VoodooDollBlockEntity 实例
     * <p> 通过指定的块位置和块状态创建 VoodooDollBlockEntity 对象, 该实体基于预定义的 VoodooDoll 实体类型进行初始化.
     * @param blockPos   块的位置
     * @param blockState 块的状态
     */
    public VoodooDollBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(EvoodooersBlockEntities.VOODOO_DOLL.get(), blockPos, blockState);
    }

    /**
     * 初始化系统组件, 设置服务和执行器
     * <p> 此方法用于配置系统所需的服务和执行器, 包括用户资料缓存, 会话服务以及主线程执行器.
     * @param services 提供系统所需服务的接口
     * @param executor 用于执行任务的执行器
     */
    public static void setup(Services services, Executor executor) {
        profileCache = services.profileCache();
        sessionService = services.sessionService();
        mainThreadExecutor = executor;
    }

    /**
     * 清除所有缓存和引用
     * <p> 将 profileCache,sessionService 和 mainThreadExecutor 设置为 null, 释放相关资源
     * @since 1.0
     */
    public static void clear() {
        profileCache = null;
        sessionService = null;
        mainThreadExecutor = null;
    }

    /**
     * 更新游戏资料并调用消费者处理
     * <p> 根据提供的游戏资料, 获取最新的游戏资料并传递给消费者. 如果提供的资料为 null, 则直接传递 null 给消费者.
     * @param gameProfile     游戏资料对象, 可以为 null
     * @param profileConsumer 消费者, 用于处理获取到的游戏资料
     */
    public static void updateGameProfile(@Nullable GameProfile gameProfile, Consumer<GameProfile> profileConsumer) {
        if (gameProfile != null) {
            SkullBlockEntity.fetchGameProfile(gameProfile.getId()).thenAcceptAsync(optionalProfile -> profileConsumer.accept(optionalProfile.orElse(gameProfile)), mainThreadExecutor);
        } else {
            profileConsumer.accept(null);
        }
    }

    /**
     * 保存附加数据到 NBT 标签中
     * <p> 该方法用于保存附加数据到 NBT 标签, 包括连接的玩家 UUID 信息. 如果拥有者不为空, 则将其编码并存储到指定的标签中.
     * @param tag        NBT 标签, 用于存储附加数据
     * @param registries 注册表提供者, 用于获取注册表信息
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.owner != null) {
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, new ResolvableProfile(this.owner))
                    .resultOrPartial(LOG::error)
                    .ifPresent(result -> tag.put(TagEnum.CONNECTED_PLAYER_UUID.get(), result));
        }
    }

    /**
     * 加载附加数据到实体中
     * <p> 该方法用于从 NBT 标签中加载额外的数据, 如连接的玩家 UUID. 如果标签中包含连接玩家 UUID 的信息, 则解析并设置拥有者.
     * @param tag        包含附加数据的 NBT 标签
     * @param registries 注册表提供者, 用于解析数据
     */
    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TagEnum.CONNECTED_PLAYER_UUID.get(), 10)) {
            ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag.getCompound(TagEnum.CONNECTED_PLAYER_UUID.get()))
                    .resultOrPartial(LOG::error)
                    .map(ResolvableProfile::gameProfile)
                    .ifPresent(this::setOwner);
        }
    }

    /**
     * 获取用于更新的标签数据
     * <p> 创建并返回一个包含当前实体附加数据的 CompoundTag, 用于后续的实体更新操作.
     * @param registries 注册表提供者, 用于获取必要的注册信息
     * @return 包含附加数据的 CompoundTag 对象, 不会为 null
     */
    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    /**
     * 获取用于更新区块实体的数据包
     * <p> 创建并返回一个用于更新区块实体的客户端数据包, 包含当前实体的状态信息
     * @return 用于更新区块实体的客户端数据包
     */
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * 获取当前游戏的拥有者资料
     * <p> 返回当前游戏的拥有者资料对象, 如果拥有者未设置则返回 null
     * @return 游戏拥有者资料, 如果拥有者未设置则返回 null
     */
    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    public void setOwner(@Nullable GameProfile profile) {
        synchronized (this) {
            this.owner = profile;
        }

        this.updateOwnerProfile();
    }

    /**
     * 更新拥有者资料
     * <p> 调用 updateGameProfile 方法更新拥有者资料, 并在更新成功后设置已更改状态, 若在服务器端则发送区块更新事件.
     *
     */
    private void updateOwnerProfile() {
        updateGameProfile(this.owner, (profile) -> {
            this.owner = profile;
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        });
    }
}
