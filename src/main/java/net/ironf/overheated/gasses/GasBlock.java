package net.ironf.overheated.gasses;

import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class GasBlock extends AbstractGlassBlock {
    public GasBlock(Properties p, GasFlowGetter gfg, Predicate<BlockState> flowThroughTest, int lowerTickDelay, int upperTickDelay) {
        super(p);
        this.gasFlowGetter = gfg;
        this.flowThroughTest = flowThroughTest;

        this.upperTickDelay = upperTickDelay;
        this.lowerTickDelay = lowerTickDelay;
    }

    protected final GasFlowGetter gasFlowGetter;
    protected final Predicate<BlockState> flowThroughTest;
    protected final int upperTickDelay;
    protected final int lowerTickDelay;

    public static final IntegerProperty FORCEDMOVEMENT =
            IntegerProperty.create("forcedmovement",0,6);


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FORCEDMOVEMENT));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FORCEDMOVEMENT,0);
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState other_state, boolean bool) {
        super.onPlace(state, level, pos, other_state, bool);
        if (level.random.nextIntBetweenInclusive(1,8) == 8){
            level.setBlock(pos,state.setValue(FORCEDMOVEMENT,0),3);
        }
        level.scheduleTick(pos, this,level.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay));
    }


    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        if (state.getValue(FORCEDMOVEMENT) == 0){
            flowInto(gasFlowGetter.flowGas(randomSource,pos,world),state,world,pos,randomSource);
        } else {
            int forcedMove = state.getValue(FORCEDMOVEMENT);
            BlockPos target = pos.relative(Iterate.directions[forcedMove-1]);
            flowInto(target,state,world,pos,randomSource);
            world.scheduleTick(target,this,AllConfigs.server().kinetics.fanBlockCheckRate.get());
        }
    }

    public void flowInto(BlockPos target, @NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource){
        if (world.isInWorldBounds(target)) {
            BlockState targetState = world.getBlockState(target);
            if (flowThroughTest.test(targetState)) {
                world.setBlockAndUpdate(target, world.getBlockState(pos));
                world.setBlockAndUpdate(pos, targetState);
            } else {
                world.scheduleTick(pos, this, world.random.nextIntBetweenInclusive(lowerTickDelay, upperTickDelay), TickPriority.NORMAL);
            }
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }



}
