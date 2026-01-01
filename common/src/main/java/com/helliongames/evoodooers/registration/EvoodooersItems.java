package com.helliongames.evoodooers.registration;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.item.HairTuftItem;
import com.helliongames.evoodooers.item.VoodooDollBlockItem;
import com.helliongames.evoodooers.registration.util.RegistrationProvider;
import com.helliongames.evoodooers.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

/**
 * 物品注册类
 * <p> 用于注册模组中的各种物品, 包括头发束物品, 巫毒娃娃物品以及白天唱片物品等
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class EvoodooersItems {

    /**
     * The provider for items
     */
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MOD_ID);
    /** 白天播放的歌曲资源键 */
    public static final ResourceKey<JukeboxSong> DAY_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "day"));
    /** HAIR_TUFT 注册项, 用于注册头发绒毛物品 */
    public static final RegistryObject<HairTuftItem> HAIR_TUFT = ITEMS.register("hair_tuft", () -> new HairTuftItem(new Item.Properties()));
    /** 注册诅咒娃娃物品 */
    public static final RegistryObject<VoodooDollBlockItem> VOODOO_DOLL = ITEMS.register("voodoo_doll", () -> new VoodooDollBlockItem(EvoodooersBlocks.VOODOO_DOLL.get(), new Item.Properties()));
    /** 白天记录物品注册对象, 用于在游戏中注册白天记录物品 */
    public static final RegistryObject<Item> DAY_RECORD = ITEMS.register("day_record", () ->
            new Item(new Item.Properties()
                    .stacksTo(1)
                    .jukeboxPlayable(DAY_SONG)
            )
    );

    // Called in the mod initializer / constructor in order to make sure that items are registered

    /**
     * 确保物品已注册的加载类方法
     * <p> 该方法在模块初始化器或构造函数中调用, 以确保物品已被正确注册
     * @since 1.0
     */
    public static void loadClass() {
    }
}
