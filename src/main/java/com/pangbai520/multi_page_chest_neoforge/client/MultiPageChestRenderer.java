package com.pangbai520.multi_page_chest_neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pangbai520.multi_page_chest_neoforge.MultiPageChestNeoForge;
import com.pangbai520.multi_page_chest_neoforge.block.MultiPageChestBlock;
import com.pangbai520.multi_page_chest_neoforge.blockentity.MultiPageChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class MultiPageChestRenderer implements BlockEntityRenderer<MultiPageChestBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MultiPageChestNeoForge.MODID, "textures/entity/multi_page_chest.png"
    );

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public MultiPageChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ModelLayers.CHEST);
        lid = root.getChild("lid");
        bottom = root.getChild("bottom");
        lock = root.getChild("lock");
    }

    @Override
    public void render(
            MultiPageChestBlockEntity chest,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = chest.getBlockState();
        Direction facing = state.hasProperty(MultiPageChestBlock.FACING)
                ? state.getValue(MultiPageChestBlock.FACING)
                : Direction.SOUTH;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = chest.getOpenNess(partialTick);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;
        lid.xRot = -(openness * (float) (Math.PI / 2.0));
        lock.xRot = lid.xRot;

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        lid.render(poseStack, consumer, packedLight, packedOverlay);
        lock.render(poseStack, consumer, packedLight, packedOverlay);
        bottom.render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
