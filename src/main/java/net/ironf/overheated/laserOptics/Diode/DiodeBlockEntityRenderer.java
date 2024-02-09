package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class DiodeBlockEntityRenderer extends KineticBlockEntityRenderer<DiodeBlockEntity> {

    public DiodeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(DiodeBlockEntity be, BlockState state) {
        return CachedBufferer.partial(AllPartialModels.ARM_COG, state);
    }
}
