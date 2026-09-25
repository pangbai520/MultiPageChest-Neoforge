package com.pangbai520.multi_page_chest_neoforge.client;

import com.pangbai520.multi_page_chest_neoforge.MultiPageChestNeoForge;
import com.pangbai520.multi_page_chest_neoforge.blockentity.MultiPageChestBlockEntity;
import com.pangbai520.multi_page_chest_neoforge.menu.MultiPageChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class MultiPageChestScreen extends AbstractContainerScreen<MultiPageChestMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MultiPageChestNeoForge.MODID, "textures/gui/multipagechest.png"
    );
    private Button previousPageButton;
    private Button nextPageButton;

    public MultiPageChestScreen(MultiPageChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 256;
        imageHeight = 256;
    }

    @Override
    protected void init() {
        super.init();
        previousPageButton = Button.builder(Component.literal("-"), button -> switchPage(-1))
                .bounds(leftPos + 23, topPos + 173, 20, 20)
                .build();
        nextPageButton = Button.builder(Component.literal("+"), button -> switchPage(1))
                .bounds(leftPos + 213, topPos + 173, 20, 20)
                .build();
        addRenderableWidget(previousPageButton);
        addRenderableWidget(nextPageButton);
    }

    private void switchPage(int direction) {
        int buttonId = direction < 0
                ? MultiPageChestMenu.PREVIOUS_PAGE_BUTTON
                : MultiPageChestMenu.NEXT_PAGE_BUTTON;
        menu.setPage(menu.getPage() + direction);
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
        if (previousPageButton != null) {
            previousPageButton.setFocused(false);
        }
        if (nextPageButton != null) {
            nextPageButton.setFocused(false);
        }
        clearFocus();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(228, 232, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.translatable("gui.multipagechest.destroy_item"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String pageText = (menu.getPage() + 1) + " / " + MultiPageChestBlockEntity.PAGE_COUNT;
        graphics.drawString(font, pageText, 229 - font.width(pageText) / 2, 200, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
