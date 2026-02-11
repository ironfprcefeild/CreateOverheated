package net.ironf.overheated.steamworks.blocks.turbine.turbineFan;

import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class turbineFanBlock extends DirectionalBlock implements IBE<turbineFanBlockEntity> {
    public turbineFanBlock(Properties p_52591_) {
        super(p_52591_);
    }


    //Block State
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }


    //BE
    @Override
    public Class<turbineFanBlockEntity> getBlockEntityClass() {
        return turbineFanBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends turbineFanBlockEntity> getBlockEntityType() {
        return AllBlockEntities.TURBINE_FAN.get();
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        blockUpdate(state, worldIn, pos);
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
        super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
        blockUpdate(stateIn, worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        blockUpdate(state, worldIn, pos);
    }

    protected void blockUpdate(BlockState state, LevelAccessor worldIn, BlockPos pos) {
        if (worldIn instanceof WrappedLevel)
            return;
        notifyFanBlockEntity(worldIn, pos);
    }

    protected void notifyFanBlockEntity(LevelAccessor world, BlockPos pos) {
        withBlockEntityDo(world, pos, turbineFanBlockEntity::blockInFrontChanged);
    }

}
