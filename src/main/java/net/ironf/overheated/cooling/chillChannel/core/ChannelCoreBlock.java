package net.ironf.overheated.cooling.chillChannel.core;

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

public class ChannelCoreBlock extends Block implements IBE<ChannelCoreBlockEntity> {
    public ChannelCoreBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<ChannelCoreBlockEntity> getBlockEntityClass() {
        return ChannelCoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChannelCoreBlockEntity> getBlockEntityType() {
        return AllBlockEntities.CHANNEL_CORE.get();
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
        return this.defaultBlockState().setValue(FACING, (context.getPlayer() != null && context.getPlayer().isCrouching()) ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }
}

