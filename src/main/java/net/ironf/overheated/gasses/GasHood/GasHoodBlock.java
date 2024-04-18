package net.ironf.overheated.gasses.GasHood;

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
import org.jetbrains.annotations.Nullable;

public class GasHoodBlock extends Block implements IBE<GasHoodBlockEntity> {
    public GasHoodBlock(Properties p) {
        super(p);
    }


    //Block State
    public static final DirectionProperty FACING = BlockStateProperties.VERTICAL_DIRECTION;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(FACING, pContext.getNearestLookingVerticalDirection().getOpposite());
    }


    public static Direction getAttachedDirection(BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public Class<GasHoodBlockEntity> getBlockEntityClass() {
        return GasHoodBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GasHoodBlockEntity> getBlockEntityType() {
        return AllBlockEntities.GAS_HOOD.get();
    }
}
