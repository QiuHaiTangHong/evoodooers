package com.helliongames.evoodooers.mixin.client;

import com.helliongames.evoodooers.entity.block.VoodooDollBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "doWorldLoad",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;setup(Lnet/minecraft/server/Services;Ljava/util/concurrent/Executor;)V")
    )
    private void evoodooers$setupVoodooDollOnWorldLoad(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci, @Local Services services) {
        VoodooDollBlockEntity.setup(services, (Minecraft) (Object) this);
    }

    @Inject(method = "setLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;setup(Lnet/minecraft/server/Services;Ljava/util/concurrent/Executor;)V")
    )
    private void evoodooers_setupVoodooDollOnSetLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci, @Local Services services) {
        VoodooDollBlockEntity.setup(services, (Minecraft) (Object) this);
    }

    @Inject(method = "clearClientLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;clear()V"))
    private void evoodooers_clearVoodooDollOnClear(Screen $$0, CallbackInfo ci) {
        VoodooDollBlockEntity.clear();
    }
}
