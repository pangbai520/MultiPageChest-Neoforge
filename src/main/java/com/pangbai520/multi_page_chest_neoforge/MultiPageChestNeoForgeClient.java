package com.pangbai520.multi_page_chest_neoforge;

import com.pangbai520.multi_page_chest_neoforge.client.MultiPageChestItemRenderer;
import com.pangbai520.multi_page_chest_neoforge.client.MultiPageChestRenderer;
import com.pangbai520.multi_page_chest_neoforge.client.MultiPageChestScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = MultiPageChestNeoForge.MODID, value = Dist.CLIENT)
public final class MultiPageChestNeoForgeClient {
    private MultiPageChestNeoForgeClient() {
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MultiPageChestNeoForge.MULTI_PAGE_CHEST_MENU.get(), MultiPageChestScreen::new);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                MultiPageChestNeoForge.MULTI_PAGE_CHEST_BLOCK_ENTITY.get(),
                MultiPageChestRenderer::new
        );
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new MultiPageChestItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, MultiPageChestNeoForge.MULTI_PAGE_CHEST_ITEM.get());
    }
}
