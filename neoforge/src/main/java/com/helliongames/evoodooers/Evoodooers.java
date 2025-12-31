package com.helliongames.evoodooers;

import com.helliongames.evoodooers.client.ClientClass;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * NeoForge 主类
 */
@Mod(Constants.MOD_ID)
public class Evoodooers {

    public Evoodooers(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        CommonClass.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientClass::init);
    }
}