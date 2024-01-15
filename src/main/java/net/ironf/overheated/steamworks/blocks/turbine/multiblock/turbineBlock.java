package net.ironf.overheated.steamworks.blocks.turbine.multiblock;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.util.ForgeSoundType;

import javax.annotation.Nullable;
import java.util.Optional;

public class turbineBlock extends Block implements IWrenchable, IBE<turbineBlockEntity> {


    public static final Property<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty LARGE = BooleanProperty.create("large");

    public turbineBlock(Properties p_49795_) {
        super(p_49795_);
        registerDefaultState(defaultBlockState().setValue(LARGE, false));
    }

    //Blockstate management
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HORIZONTAL_AXIS, LARGE);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (pContext.getPlayer() == null || !pContext.getPlayer()
                .isShiftKeyDown()) {
            BlockState placedOn = pContext.getLevel()
                    .getBlockState(pContext.getClickedPos()
                            .relative(pContext.getClickedFace()
                                    .getOpposite()));
            Direction.Axis preferredAxis = getTurbineBlockAxis(placedOn);
            if (preferredAxis != null)
                return this.defaultBlockState()
                        .setValue(HORIZONTAL_AXIS, preferredAxis);
        }
        return this.defaultBlockState()
                .setValue(HORIZONTAL_AXIS, pContext.getHorizontalDirection()
                        .getAxis());
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() == pState.getBlock())
            return;
        if (pIsMoving)
            return;
        withBlockEntityDo(pLevel, pPos, turbineBlockEntity::updateConnectivity);
    }


    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace()
                .getAxis()
                .isVertical()) {
            BlockEntity be = context.getLevel()
                    .getBlockEntity(context.getClickedPos());
            if (be instanceof turbineBlockEntity) {
                turbineBlockEntity turbine = (turbineBlockEntity) be;
                ConnectivityHandler.splitMulti(turbine);
                turbine.removeController(true);
            }
            state = state.setValue(LARGE, false);
        }
        InteractionResult onWrenched = IWrenchable.super.onWrenched(state, context);
        return onWrenched;
    }


    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean pIsMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof turbineBlockEntity))
                return;
            turbineBlockEntity turbineBE = (turbineBlockEntity) be;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(turbineBE);
        }
    }

    //Helper Functions



    @Nullable
    public static Direction.Axis getTurbineBlockAxis(BlockState state) {
        if (!isTurbine(state))
            return null;
        return state.getValue(HORIZONTAL_AXIS);
    }

    public static boolean isLarge(BlockState state) {
        if (!isTurbine(state))
            return false;
        return state.getValue(LARGE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        Direction.Axis axis = state.getValue(HORIZONTAL_AXIS);
        return state.setValue(HORIZONTAL_AXIS, rot.rotate(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))
                .getAxis());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        withBlockEntityDo(p_60544_,p_60545_,turbineBlockEntity::updateFlowDirection);
        return super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
    }

    //Special Sounds
    public static final SoundType SILENCED_METAL =
            new ForgeSoundType(0.1F, 1.5F, () -> SoundEvents.NETHERITE_BLOCK_BREAK, () -> SoundEvents.NETHERITE_BLOCK_STEP,
                    () -> SoundEvents.NETHERITE_BLOCK_PLACE, () -> SoundEvents.NETHERITE_BLOCK_HIT,
                    () -> SoundEvents.NETHERITE_BLOCK_FALL);

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData()
                .contains("SilenceTurbineSound"))
            return SILENCED_METAL;
        return soundType;
    }

    //Updating Turbine States

    public static Optional<turbineBlockEntity> getTurbineHelper(LevelReader pLevel, BlockPos turbinePos,boolean getController) {
        BlockState turbineState = pLevel.getBlockState(turbinePos);
        if (!(turbineState.getBlock() instanceof turbineBlock turbine)) {
            return Optional.empty();
        }
        turbineBlockEntity turbineBE = turbine.getBlockEntity(pLevel, turbinePos);
        if (turbineBE == null) {
            return Optional.empty();
        }
        if (!getController){
            return Optional.of(turbineBE);
        }
        turbineBlockEntity controllerBE = turbineBE.getControllerBE();
        if (controllerBE == null) {
            return Optional.empty();
        }
        return Optional.of(controllerBE);
    }
    public static void updateTurbineState(BlockState pState, Level pLevel, BlockPos turbinePos, Optional<BlockPos> attachmentPos, boolean isVent, boolean isRemoval) {
        Optional<turbineBlockEntity> controllerBE = getTurbineHelper(pLevel,turbinePos,true);
        if (controllerBE.isPresent() && !isRemoval) {
            controllerBE.get().updateFlowDirection(attachmentPos, isVent);
        }
    }
    public static boolean isTurbine(BlockState state) {
        return AllBlocks.TURBINE.has(state);
    }
    public static boolean isTurbineEdge(BlockState state, LevelReader pLevel, BlockPos turbinePos) {
        Optional<turbineBlockEntity> preTurbine = getTurbineHelper(pLevel,turbinePos,false);
        if (preTurbine.isEmpty() || !isTurbine(state)){
            return false;
        }
        //Overheated.LOGGER.info(String.valueOf(preTurbine.get().isOnEdge()));
        return preTurbine.get().isOnEdge();
    }



    //BE management

    @Override
    public Class<turbineBlockEntity> getBlockEntityClass() {
        return turbineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends turbineBlockEntity> getBlockEntityType() {
        return AllBlockEntities.TURBINE.get();
    }
}
