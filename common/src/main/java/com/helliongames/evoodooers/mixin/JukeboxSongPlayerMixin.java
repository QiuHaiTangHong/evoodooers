package com.helliongames.evoodooers.mixin;

import com.helliongames.evoodooers.registration.EvoodooersItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * JukeboxSongPlayerMixin 混入类
 * <p> 用于扩展 JukeboxSongPlayer 的功能, 实现自定义歌曲播放逻辑. 该混入类通过注入方式修改 tick 方法, 当播放特定歌曲时触发自定义播放行为.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mixin(JukeboxSongPlayer.class)
public abstract class JukeboxSongPlayerMixin {
    /** 歌曲信息持有者, 用于存储当前播放的歌曲数据 */
    @Shadow
    @Nullable
    private Holder<JukeboxSong> song;

    /**
     * 在指定世界中播放指定的音乐曲目
     * <p> 该方法用于在给定的世界中播放指定的音乐曲目, 通常用于游戏中的音效播放功能.
     * @param level 世界访问器, 用于获取世界信息和进行世界相关的操作
     * @param song  要播放的音乐曲目, 通过 Holder 包装以支持曲目资源的获取和管理
     */
    @Shadow
    public abstract void play(LevelAccessor level, Holder<JukeboxSong> song);

    /**
     * 自定义唱片循环播放逻辑
     * <p> 在调用 JukeboxSongPlayer 的 stop 方法时, 检查当前播放的歌曲是否为指定的白天歌曲, 如果是则播放该歌曲并取消后续操作
     * @param level 世界访问器, 用于获取世界信息
     * @param state 块状态, 可能为 null
     * @param ci    回调信息, 用于控制是否取消后续操作
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSongPlayer;stop(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;)V"), cancellable = true)
    private void evoodooers$loopCustomRecord(LevelAccessor level, @Nullable BlockState state, CallbackInfo ci) {
        if (this.song != null && this.song.is(EvoodooersItems.DAY_SONG)) {
            this.play(level, this.song);
            ci.cancel();
        }
    }
}
