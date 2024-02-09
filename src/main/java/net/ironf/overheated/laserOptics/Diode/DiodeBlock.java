package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.common.property.Properties;

public class DiodeBlock extends KineticBlock implements IBE<DiodeBlockEntity>, ICogWheel {
    public DiodeBlock(Properties properties) {
        super(properties);
    }

    //Block State
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction face = pContext.getClickedFace();
        if (face.getAxis() == Direction.Axis.Y) {
            face = pContext.getHorizontalDirection()
                    .getOpposite();
        }
        return super.getStateForPlacement(pContext).setValue(FACING, face.getOpposite());
    }

    //BE
    @Override
    public Class<DiodeBlockEntity> getBlockEntityClass() {
        return DiodeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DiodeBlockEntity> getBlockEntityType() {
        return AllBlockEntities.DIODE.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }
}
