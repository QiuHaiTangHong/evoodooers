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

@Mixin(JukeboxSongPlayer.class)
public abstract class JukeboxSongPlayerMixin {
    @Shadow
    @Nullable
    private Holder<JukeboxSong> song;

    @Shadow
    public abstract void play(LevelAccessor level, Holder<JukeboxSong> song);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSongPlayer;stop(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;)V"), cancellable = true)
    private void evoodooers$loopCustomRecord(LevelAccessor level, @Nullable BlockState state, CallbackInfo ci) {
        if (this.song != null && this.song.is(EvoodooersItems.DAY_SONG)) {
            this.play(level, this.song);
            ci.cancel();
        }
    }
}
