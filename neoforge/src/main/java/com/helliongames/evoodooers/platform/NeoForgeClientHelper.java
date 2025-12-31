package com.helliongames.evoodooers.platform;

import com.helliongames.evoodooers.client.render.VoodooDollRenderer;
import com.helliongames.evoodooers.platform.services.IClientHelper;
import com.helliongames.evoodooers.registration.EvoodooersBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 用于自定义渲染逻辑
 */
@EventBusSubscriber(modid = "evoodooers", value = Dist.CLIENT)
public class NeoForgeClientHelper implements IClientHelper {
    @SubscribeEvent
    public static void registerModelLayerListener(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void registerEntityRendererListener(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EvoodooersBlockEntities.VOODOO_DOLL.get(), VoodooDollRenderer::new);
    }

    @Override
    public void registerEntityRenderers() {
    }

    @Override
    public void registerModelLayers() {
    }

    @Override
    public void registerRenderTypes() {
    }
}
