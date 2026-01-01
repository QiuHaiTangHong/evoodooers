package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * NeoForge 注册工厂类
 * <p> 用于创建 NeoForge 的注册提供者, 支持 FML 模组的注册功能, 提供注册对象的创建和管理
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class NeoForgeRegistrationFactory implements RegistrationProvider.Factory {

    /**
     * 创建一个注册提供者, 用于在指定模组中注册资源
     * <p> 根据给定的资源键和模组 ID 创建注册提供者, 如果模组容器不存在则抛出异常.
     * @param resourceKey 资源键, 用于指定要注册的资源类型
     * @param modId       模组 ID, 用于标识模组
     * @return 注册提供者, 用于在模组中注册资源
     */
    @Override
    public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        final var containerOpt = ModList.get().getModContainerById(modId);
        if (containerOpt.isEmpty())
            throw new NullPointerException("Cannot find mod container for id " + modId);
        final var cont = containerOpt.get();
        if (cont instanceof FMLModContainer fmlModContainer) {
            final var register = DeferredRegister.create(resourceKey, modId);
            if (fmlModContainer.getEventBus() != null) {
                register.register(fmlModContainer.getEventBus());
            }
            return new Provider<>(modId, register);
        } else {
            throw new ClassCastException("The container of the mod " + modId + " is not a FML one!");
        }
    }

    /**
     * 注册提供者类
     * <p> 用于管理注册项的注册和获取, 支持通过模组 ID 和注册表进行对象注册, 并提供注册项的集合视图
     * @author zeka.stack.team
     * @version 1.0.0
     * @email "mailto:zeka.stack@gmail.com"
     * @date 2026.01.01
     * @since 1.0.0
     */
    private static class Provider<T> implements RegistrationProvider<T> {
        /** 模组唯一标识符 */
        private final String modId;
        /** 用于注册延迟初始化的 bean 注册表 */
        private final DeferredRegister<T> registry;

        /** 用于存储注册条目的集合 */
        private final Set<RegistryObject<T>> entries = new HashSet<>();
        /** entriesView 是 entries 的不可变视图, 用于防止外部直接修改内部集合 */
        private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

        /**
         * 初始化 Provider 实例
         * <p> 用于设置模块 ID 和注册表, 以便后续注册对象到对应的注册表中
         * @param modId    模块 ID, 用于标识所属模块
         * @param registry 注册表, 用于注册对象
         */
        private Provider(String modId, DeferredRegister<T> registry) {
            this.modId = modId;
            this.registry = registry;
        }

        /**
         * 获取模块 ID
         * <p> 返回当前模块的唯一标识符
         * @return 模块 ID
         */
        @Override
        public String getModId() {
            return modId;
        }

        /**
         * 注册一个带有指定名称的条目, 并返回对应的注册对象
         * <p> 该方法用于向注册表中添加新的条目, 通过名称和提供者创建注册对象, 并将其添加到条目列表中
         * @param name     条目的名称
         * @param supplier 提供条目实例的供应者
         * @return 注册对象, 可用于获取条目实例或相关资源信息
         */
        @Override
        @SuppressWarnings("unchecked")
        public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
            final DeferredHolder<T, I> obj = registry.register(name, supplier);
            final var ro = new RegistryObject<I>() {
                /**
                 * 获取资源键
                 * <p> 返回对象的资源键, 该键用于唯一标识资源
                 * @return 资源键
                 */
                @Override
                public ResourceKey<I> getResourceKey() {
                    return (ResourceKey<I>) obj.getKey();
                }

                /**
                 * 获取对象的资源定位符 (ID)
                 * <p> 返回关联对象的资源定位符, 用于唯一标识该对象
                 * @return 对象的资源定位符
                 */
                @Override
                public ResourceLocation getId() {
                    return obj.getId();
                }

                /**
                 * 调用对象的 get 方法并返回结果
                 * <p> 此方法用于获取对象的值, 通过调用内部对象的 get 方法实现
                 * @return 对象的值
                 */
                @Override
                public I get() {
                    return obj.get();
                }

                /**
                 * 将当前对象转换为 Holder 类型的持有者
                 * <p> 该方法用于将当前对象转换为 Holder<I> 类型, 以便进行后续操作或传递给需要 Holder 类型的接口或方法.
                 * @return 当前对象作为 Holder<I> 类型的持有者
                 */
                @Override
                public Holder<I> asHolder() {
                    return (Holder<I>) obj;
                }
            };
            entries.add((RegistryObject<T>) ro);
            return ro;
        }

        @Override
        public Set<RegistryObject<T>> getEntries() {
            return entriesView;
        }
    }
}