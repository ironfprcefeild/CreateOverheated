package net.ironf.overheated.steamworks.blocks.turbine.turbineCenter;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class turbineCenterBlock extends Block implements IBE<turbineCenterBlockEntity> {
    public turbineCenterBlock(Properties properties) {
        super(properties);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction face = pContext.getClickedFace();
        if (face.getAxis() == Direction.Axis.Y) {
            face = pContext.getHorizontalDirection().getOpposite();
        }
        return super.getStateForPlacement(pContext).setValue(FACING, face);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add());
    }

    @Override
    public Class<turbineCenterBlockEntity> getBlockEntityClass() {
        return turbineCenterBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends turbineCenterBlockEntity> getBlockEntityType() {
        return AllBlockEntities.TURBINE_CENTER.get();
    }
}
