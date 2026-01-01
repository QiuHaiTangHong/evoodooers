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

/**
 * Minecraft 混入类
 * <p> 用于扩展 Minecraft 的功能, 主要通过混入方式实现对世界加载, 设置级别和清除级别等操作的增强, 支持 Voodoo Doll 的初始化和清理逻辑.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    /**
     * 在世界加载时设置附魔娃娃实体
     * <p> 在世界加载过程中调用, 用于初始化附魔娃娃实体的相关配置, 确保其正确加载和运行.
     * @param levelStorage   世界存储访问对象
     * @param packRepository 包仓库, 用于加载资源包
     * @param worldStem      世界主干对象, 包含世界相关配置
     * @param newWorld       是否为新世界
     * @param ci             回调信息, 用于控制流程
     * @param services       服务对象, 提供游戏运行所需的服务
     */
    @Inject(method = "doWorldLoad",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;setup(Lnet/minecraft/server/Services;Ljava/util/concurrent/Executor;)V")
    )
    private void evoodooers$setupVoodooDollOnWorldLoad(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld, CallbackInfo ci, @Local Services services) {
        VoodooDollBlockEntity.setup(services, (Minecraft) (Object) this);
    }

    /**
     * 在设置级别时初始化附魔娃娃实体
     * <p> 当级别设置时, 调用此方法以初始化附魔娃娃实体, 使用提供的服务和当前 Minecraft 实例.
     * @param level    当前级别
     * @param reason   设置级别的原因
     * @param ci       回调信息
     * @param services 服务实例, 用于初始化附魔娃娃实体
     */
    @Inject(method = "setLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;setup(Lnet/minecraft/server/Services;Ljava/util/concurrent/Executor;)V")
    )
    private void evoodooers_setupVoodooDollOnSetLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci, @Local Services services) {
        VoodooDollBlockEntity.setup(services, (Minecraft) (Object) this);
    }

    /**
     * 在清除客户端层级时清除诅咒娃娃
     * <p> 当调用清除客户端层级方法时, 触发清除诅咒娃娃的操作, 确保与游戏机制同步.
     * @param $$0 当前屏幕对象
     * @param ci  回调信息, 用于控制流程
     */
    @Inject(method = "clearClientLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SkullBlockEntity;clear()V"))
    private void evoodooers_clearVoodooDollOnClear(Screen $$0, CallbackInfo ci) {
        VoodooDollBlockEntity.clear();
    }
}
