package net.ironf.overheated.steamworks.blocks.impactDrill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.ironf.overheated.AllPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ImpactDrillRenderer extends SafeBlockEntityRenderer<ImpactDrillBlockEntity> {

    public ImpactDrillRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public boolean shouldRenderOffScreen(ImpactDrillBlockEntity p_112306_) {
        return true;
    }
    @Override
    protected void renderSafe(ImpactDrillBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;
        float headPos = be.headPosition;
        SuperByteBuffer headRender = CachedBufferer.partial(AllPartialModels.IMPACT_DRILL_HEAD, be.getBlockState());
        headRender.translate(0, -1.2*headPos, 0)
                .light(light)
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
    }
}
