package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlockEntities;
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

public class DiodeBlock extends KineticBlock implements IBE<DiodeBlockEntity>, ICogWheel, IWrenchable {
    public DiodeBlock(Properties properties) {
        super(properties);
    }

    //Block State
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING,  context.getPlayer().isCrouching() ?  context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }


    //BE
    @Override
    public Class<DiodeBlockEntity> getBlockEntityClass() {
        return DiodeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DiodeBlockEntity> getBlockEntityType() {
        return AllBlockEntities.DIODE.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    //Wrenchable

    //Wrenching the diode will retest for clearance
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockEntity diode = context.getLevel().getBlockEntity(context.getClickedPos());
        if (diode.getType() == AllBlockEntities.DIODE.get()) {
            if (!((DiodeBlockEntity) diode).hasClearance) {
                ((DiodeBlockEntity) diode).testForClearance();
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

}
