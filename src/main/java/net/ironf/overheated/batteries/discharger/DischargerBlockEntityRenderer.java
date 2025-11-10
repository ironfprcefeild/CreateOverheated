package net.ironf.overheated.batteries.discharger;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class DischargerBlockEntityRenderer extends KineticBlockEntityRenderer<DischargerBlockEntity> {

    public DischargerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(DischargerBlockEntity be, BlockState state) {
        return CachedBuffers.partial(AllPartialModels.SHAFT_HALF,state);
    }
}
