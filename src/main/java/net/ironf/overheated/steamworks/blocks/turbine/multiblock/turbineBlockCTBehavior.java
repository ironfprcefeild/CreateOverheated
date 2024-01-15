package net.ironf.overheated.steamworks.blocks.turbine.multiblock;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class turbineBlockCTBehavior extends ConnectedTextureBehaviour.Base {
    @Override
    public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {

        Direction.Axis turbineBlockAxis = turbineBlock.getTurbineBlockAxis(state);
        boolean small = !turbineBlock.isLarge(state);
        if (turbineBlockAxis == null)
            return null;

        if (direction.getAxis() == turbineBlockAxis)
            return AllSpriteShifts.VAULT_FRONT.get(small);
        if (direction == Direction.UP)
            return AllSpriteShifts.VAULT_TOP.get(small);
        if (direction == Direction.DOWN)
            return AllSpriteShifts.VAULT_BOTTOM.get(small);

        return AllSpriteShifts.VAULT_SIDE.get(small);
    }
    //TODO create Assets for turbine and replace them here

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis turbineBlockAxis = turbineBlock.getTurbineBlockAxis(state);
        boolean alongX = turbineBlockAxis == Direction.Axis.X;
        if (face.getAxis()
                .isVertical() && alongX)
            return super.getUpDirection(reader, pos, state, face).getClockWise();
        if (face.getAxis() == turbineBlockAxis || face.getAxis()
                .isVertical())
            return super.getUpDirection(reader, pos, state, face);
        return Direction.fromAxisAndDirection(turbineBlockAxis, alongX ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis turbineBlockAxis = turbineBlock.getTurbineBlockAxis(state);
        if (face.getAxis()
                .isVertical() && turbineBlockAxis == Direction.Axis.X)
            return super.getRightDirection(reader, pos, state, face).getClockWise();
        if (face.getAxis() == turbineBlockAxis || face.getAxis()
                .isVertical())
            return super.getRightDirection(reader, pos, state, face);
        return Direction.fromAxisAndDirection(Direction.Axis.Y, face.getAxisDirection());
    }

    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return state == other && ConnectivityHandler.isConnected(reader, pos, otherPos); //ItemVaultConnectivityHandler.isConnected(reader, pos, otherPos);
    }
}
