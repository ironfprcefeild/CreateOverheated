package net.ironf.overheated.steamworks.blocks.turbine.turbineVent;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.Optional;

public class turbineVentBlock extends Block implements IBE<turbineVentBlockEntity> {
    public turbineVentBlock(Properties properties) {
        super(properties);
    }

    //Placement Assistance/Epic Block-state tomfoolery

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WALL = BooleanProperty.create("wall");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(WALL));
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        turbineBlock.updateTurbineState(pState, pLevel, pPos.relative(getAttachedDirection(pState)), Optional.of(pPos),true,false);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
        turbineBlock.updateTurbineState(pState, pLevel, pPos.relative(getAttachedDirection(pState)),Optional.of(pPos),true,true);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        //Only allows placement on the sides of the turbine
        BlockPos turbinePos = pPos.relative(getAttachedDirection(pState));
        return turbineBlock.isTurbineEdge(pLevel.getBlockState(turbinePos),pLevel,turbinePos);
    }
    public static Direction getAttachedDirection(BlockState state) {
        return state.getValue(WALL) ? state.getValue(FACING) : Direction.DOWN;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();
        Direction face = pContext.getClickedFace();
        boolean wall = true;
        if (face.getAxis() == Direction.Axis.Y) {
            face = pContext.getHorizontalDirection()
                    .getOpposite();
            wall = false;
        }

        BlockState state = super.getStateForPlacement(pContext).setValue(FACING, face.getOpposite())
                .setValue(WALL, wall);
        if (!canSurvive(state, level, clickedPos))
            return null;
        return state;
    }

    //Doing Stuff


    //Block Entity
    @Override
    public Class<turbineVentBlockEntity> getBlockEntityClass() {
        return turbineVentBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends turbineVentBlockEntity> getBlockEntityType() {
        return AllBlockEntities.TURBINE_VENT.get();
    }


}
