package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class turbineEndRenderer extends KineticBlockEntityRenderer<turbineEndBlockEntity> {

    public turbineEndRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    protected SuperByteBuffer getRotatedModel(turbineEndBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }
}
