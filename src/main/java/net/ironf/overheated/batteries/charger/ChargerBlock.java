package net.ironf.overheated.batteries.charger;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class ChargerBlock extends KineticBlock implements IBE<ChargerBlockEntity> {
    public ChargerBlock(Properties p_49795_) {
        super(p_49795_);
    }

    //BE
    @Override
    public Class<ChargerBlockEntity> getBlockEntityClass() {
        return ChargerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChargerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHARGER.get();
    }

    //BS
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING,  context.getPlayer().isCrouching() ?  context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(FACING) == face;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
}
