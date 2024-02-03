package net.ironf.overheated.laserOptics.backend.beam;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class BeamBlock extends Block implements IBE<BeamBlockEntity> {

    public BeamBlock(Properties properties) {
        super(properties);
    }

    //Block-states
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    //BE Junk

    @Override
    public Class<BeamBlockEntity> getBlockEntityClass() {
        return BeamBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeamBlockEntity> getBlockEntityType() {
        return AllBlockEntities.BEAM.get();
    }
}
