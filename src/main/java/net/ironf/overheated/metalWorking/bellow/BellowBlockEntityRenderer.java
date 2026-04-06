package net.ironf.overheated.metalWorking.bellow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BellowBlockEntityRenderer extends SafeBlockEntityRenderer<BellowBlockEntity> {
    public BellowBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    protected void renderSafe(BellowBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ///Render Bellow Part
        float headOffset = (float) ((0.15-(Math.abs(be.processingTicks-(BellowBlockEntity.bellowTicks/2)))/BellowBlockEntity.bellowTicks));
        SuperByteBuffer headRender = CachedBuffers.partial(net.ironf.overheated.AllPartialModels.BELLOW_HEAD, be.getBlockState());
        headRender.translate(0, headOffset, 0)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
