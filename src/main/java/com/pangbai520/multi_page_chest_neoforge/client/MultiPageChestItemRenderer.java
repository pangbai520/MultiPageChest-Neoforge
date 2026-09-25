package com.pangbai520.multi_page_chest_neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pangbai520.multi_page_chest_neoforge.MultiPageChestNeoForge;
import com.pangbai520.multi_page_chest_neoforge.block.MultiPageChestBlock;
import com.pangbai520.multi_page_chest_neoforge.blockentity.MultiPageChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class MultiPageChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final MultiPageChestBlockEntity itemChest = new MultiPageChestBlockEntity(
            BlockPos.ZERO,
            MultiPageChestNeoForge.MULTI_PAGE_CHEST.get().defaultBlockState()
                    .setValue(MultiPageChestBlock.FACING, Direction.SOUTH)
    );

    public MultiPageChestItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(itemChest, poseStack, buffers, packedLight, packedOverlay);
    }
}
