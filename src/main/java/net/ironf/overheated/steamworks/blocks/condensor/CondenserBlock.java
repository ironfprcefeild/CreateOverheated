package net.ironf.overheated.steamworks.blocks.condensor;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class CondenserBlock extends Block implements IBE<CondenserBlockEntity>, IWrenchable {
    public CondenserBlock(Properties p) {
        super(p);
    }
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING,  context.getPlayer().isCrouching() ?  context.getHorizontalDirection() : context.getHorizontalDirection().getOpposite());
    }

    @Override
    public Class<CondenserBlockEntity> getBlockEntityClass() {
        return CondenserBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CondenserBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CONDENSER.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CondenserBlockEntity CBE) {
            CBE.wrench();
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }
}
