package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block;

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

//TODO implement IBE
public class BlastFurnaceControllerBlock extends Block implements IBE<BlastFurnaceControllerBlockEntity> {
    public BlastFurnaceControllerBlock(Properties p_52591_) {
        super(p_52591_);
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
        return this.defaultBlockState().setValue(FACING, Horizontalize((context.getPlayer() != null && context.getPlayer().isCrouching()) ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite()));
    }

    private Direction Horizontalize(Direction direction) {
        return direction.getAxis() == Direction.Axis.Y ? direction.getClockWise(Direction.Axis.X) : direction;
    }

    @Override
    public Class<BlastFurnaceControllerBlockEntity> getBlockEntityClass() {
        return BlastFurnaceControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastFurnaceControllerBlockEntity> getBlockEntityType() {
        return AllBlockEntities.BLAST_FURNACE_CONTROLLER.get();
    }
}
