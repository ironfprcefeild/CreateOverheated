package net.ironf.overheated.steamworks.blocks.turbine.turbineFan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.ironf.overheated.AllPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.checkerframework.checker.units.qual.A;

public class turbineFanRenderer extends SafeBlockEntityRenderer<turbineFanBlockEntity> {
    public turbineFanRenderer(BlockEntityRendererProvider.Context context) {}

    //A lot of this comes from the kinetic renderer files
    @Override
    protected void renderSafe(turbineFanBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()) || !be.centerBlock)
            return;

        int radius = be.lastTurbineRadius;
        PartialModel fanModel = radius >= 8 ? AllPartialModels.LARGE_TURBINE_FAN : (radius >= 2 ? AllPartialModels.MEDIUM_TURBINE_FAN : AllPartialModels.SMALL_TURBINE_FAN);
        SuperByteBuffer bladesRender = CachedBuffers.partial(fanModel, be.getBlockState());

        bladesRender.light(light);

        Direction.Axis axis = be.getBlockState().getValue(BlockStateProperties.FACING).getAxis();
        float offset = rotationOffset(axis,be.getBlockPos());
        float angle = getAngleForBe(be,offset);
        bladesRender.rotateCentered(angle, Direction.get(Direction.AxisDirection.POSITIVE, axis));

        bladesRender.renderInto(ms,bufferSource.getBuffer(RenderType.solid()));
    }

    public static float getAngleForBe(turbineFanBlockEntity be, float offset) {
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        return ((time * be.getSpeed() / 10 + offset) % 360) / 180 * (float) Math.PI;
    }

    public static float rotationOffset(Direction.Axis axis, Vec3i pos) {
        if (shouldOffset(axis, pos)) {
            return 22.5f;
        } else {
            return 0;
        }
    }

    public static boolean shouldOffset(Direction.Axis axis, Vec3i pos) {
        // Sum the components of the other 2 axes.
        int x = (axis == Direction.Axis.X) ? 0 : pos.getX();
        int y = (axis == Direction.Axis.Y) ? 0 : pos.getY();
        int z = (axis == Direction.Axis.Z) ? 0 : pos.getZ();
        return ((x + y + z) % 2) == 0;
    }
}
