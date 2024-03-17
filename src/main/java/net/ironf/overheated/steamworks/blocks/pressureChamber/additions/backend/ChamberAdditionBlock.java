package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class ChamberAdditionBlock extends Block {

    public ChamberAdditionBlock(Properties p_49795_) {
        super(p_49795_);
    }

    //Block State
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return ChamberCoreBlock.isCore(pLevel.getBlockState(pPos.relative(getAttachedDirection(pState))));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();
        Direction face = pContext.getClickedFace();
        BlockState state = super.getStateForPlacement(pContext).setValue(FACING, face.getOpposite());
        if (!canSurvive(state, level, clickedPos))
            return null;
        return state;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        Overheated.LOGGER.info("detected placed addition");
        ChamberCoreBlock.updateChamberState(pState, pLevel, pPos.relative(getAttachedDirection(pState)));
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        ChamberCoreBlock.updateChamberState(pState, pLevel, pPos.relative(getAttachedDirection(pState)));
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    public static Direction getAttachedDirection(BlockState state) {
        return state.getValue(FACING);
    }
}
