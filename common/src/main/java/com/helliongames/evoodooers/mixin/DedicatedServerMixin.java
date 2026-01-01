package com.helliongames.evoodooers.mixin;

import com.helliongames.evoodooers.entity.block.VoodooDollBlockEntity;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

/**
 * 用于扩展 Minecraft 服务器功能的混入类
 * <p> 该类通过混入方式为 DedicatedServer 添加额外的服务器初始化和停止逻辑, 主要处理与 VoodooDollBlockEntity 相关的配置和清理操作
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServer {
    public DedicatedServerMixin(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, Services $$6, ChunkProgressListenerFactory $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    /**
     * 注入方法, 用于初始化诅咒娃娃服务器
     * <p> 在调用 SkullBlockEntity 的 setup 方法时执行, 用于设置诅咒娃娃的服务器信息
     * @param cir 回调信息, 用于获取方法返回值
     */
    @Inject(method = "initServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;setup(Lnet/minecraft/server/Services;Ljava/util/concurrent/Executor;)V"))
    private void evoodooers_setupVoodooDollServer(CallbackInfoReturnable<Boolean> cir) {
        VoodooDollBlockEntity.setup(this.services, this);
    }

    /**
     * 在服务器停止时清除所有巫术娃娃
     * <p> 该方法在服务器停止时被调用, 用于清除所有巫术娃娃的缓存或状态, 确保资源正确释放.
     * @param ci 回调信息, 用于控制方法执行流程
     */
    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;clear()V"))
    private void evoodooers_clearVoodooDollOnServerStop(CallbackInfo ci) {
        VoodooDollBlockEntity.clear();
    }
}
