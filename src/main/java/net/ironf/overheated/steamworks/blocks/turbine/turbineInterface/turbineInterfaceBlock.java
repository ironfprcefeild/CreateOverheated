package net.ironf.overheated.steamworks.blocks.turbine.turbineInterface;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
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

public class turbineInterfaceBlock extends Block implements IBE<turbineInterfaceBlockEntity> {
    public turbineInterfaceBlock(Properties properties) {
        super(properties);
    }

    //Block State tomfoolery and placement assistance (expect this is placed onto shafts or vents, not turbines)
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WALL = BooleanProperty.create("wall");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(WALL));
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        BlockState attachmentState = pLevel.getBlockState(pPos.relative(getAttachedDirection(pState)));
        Optional<turbineInterfaceBlockEntity> beOp = getBlockEntityOptional(pLevel,pPos);
        if (beOp.isEmpty()){
            return;
        }
        if (AllBlocks.TURBINE_VENT.has(attachmentState)){
            beOp.get().setOnVent(true);
        } else if (AllBlocks.TURBINE_SHAFT.has(attachmentState)){
            beOp.get().setOnVent(false);
        } else {
            beOp.get().setOnVent(null);
        }

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        //Only allows placement on the sides of the turbine
        BlockState attachmentState = pLevel.getBlockState(pPos.relative(getAttachedDirection(pState)));
        return AllBlocks.TURBINE_VENT.has(attachmentState) || AllBlocks.TURBINE_SHAFT.has(attachmentState);
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


    //BE management

    @Override
    public Class<turbineInterfaceBlockEntity> getBlockEntityClass() {
        return turbineInterfaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends turbineInterfaceBlockEntity> getBlockEntityType() {
        return AllBlockEntities.TURBINE_INTERFACE.get();
    }

}
