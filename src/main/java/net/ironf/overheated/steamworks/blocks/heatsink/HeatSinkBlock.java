package net.ironf.overheated.steamworks.blocks.heatsink;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class HeatSinkBlock extends Block implements IBE<HeatSinkBlockEntity> {
    public HeatSinkBlock(Properties p) {
        super(p);
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
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }
    //Block Entity

    @Override
    public Class<HeatSinkBlockEntity> getBlockEntityClass() {
        return HeatSinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HeatSinkBlockEntity> getBlockEntityType() {
        return AllBlockEntities.HEAT_SINK.get();
    }
}
