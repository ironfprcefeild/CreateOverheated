package net.ironf.overheated.laserOptics.mirrors.splitMirror;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.laserOptics.Diode.DiodeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class SplitMirrorBlock extends Block implements IBE<SplitMirrorBlockEntity> {
    public SplitMirrorBlock(Properties p) {
        super(p);
    }


    //Block State
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getPlayer().isCrouching() ?  context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING,dir.getAxis() == Direction.Axis.Y ? Direction.EAST : dir);
    }



    //BE

    @Override
    public Class<SplitMirrorBlockEntity> getBlockEntityClass() {
        return SplitMirrorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SplitMirrorBlockEntity> getBlockEntityType() {
        return AllBlockEntities.SPLIT_MIRROR.get();
    }
}
