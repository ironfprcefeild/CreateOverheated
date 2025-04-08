package net.ironf.overheated.cooling.coolingTower;

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

public class CoolingTowerBlock extends Block implements IBE<CoolingTowerBlockEntity> {
    public CoolingTowerBlock(Properties p) {
        super(p);
    }

    @Override
    public Class<CoolingTowerBlockEntity> getBlockEntityClass() {
        return CoolingTowerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CoolingTowerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.COOLING_TOWER.get();
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
        Direction facing = (context.getPlayer() != null && context.getPlayer().isCrouching()) ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite();
        if (facing == Direction.UP || facing == Direction.DOWN){
            facing = context.getPlayer().isCrouching() ? context.getHorizontalDirection().getOpposite() : context.getNearestLookingDirection();
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }
}
