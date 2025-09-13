package net.ironf.overheated.cooling.cooler;

import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class CoolerBlock extends Block implements IBE<CoolerBlockEntity> {
    public CoolerBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<CoolerBlockEntity> getBlockEntityClass() {
        return CoolerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CoolerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.COOLER.get();
    }


    //Block State
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, (context.getPlayer() != null && context.getPlayer().isCrouching()) ? context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite());
    }
}
