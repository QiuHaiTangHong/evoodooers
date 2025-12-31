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

public class VoodooDollBlockEntity extends BlockEntity {
    @Nullable
    private static GameProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private GameProfile owner;

    public VoodooDollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public VoodooDollBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(EvoodooersBlockEntities.VOODOO_DOLL.get(), blockPos, blockState);
    }

    public static void setup(Services services, Executor executor) {
        profileCache = services.profileCache();
        sessionService = services.sessionService();
        mainThreadExecutor = executor;
    }

    public static void clear() {
        profileCache = null;
        sessionService = null;
        mainThreadExecutor = null;
    }

    public static void updateGameProfile(@Nullable GameProfile gameProfile, Consumer<GameProfile> profileConsumer) {
        if (gameProfile != null) {
            SkullBlockEntity.fetchGameProfile(gameProfile.getId()).thenAcceptAsync(optionalProfile -> profileConsumer.accept(optionalProfile.orElse(gameProfile)), mainThreadExecutor);
        } else {
            profileConsumer.accept(null);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.owner != null) {
            ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, new ResolvableProfile(this.owner))
                    .resultOrPartial(LOG::error)
                    .ifPresent(result -> tag.put(TagEnum.CONNECTED_PLAYER_UUID.get(), result));
        }
    }

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

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

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
