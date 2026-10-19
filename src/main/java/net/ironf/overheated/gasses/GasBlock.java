package net.ironf.overheated.gasses;

import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class GasBlock extends TintedGlassBlock {
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



    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState other_state, boolean bool) {
        super.onPlace(state, level, pos, other_state, bool);
        level.scheduleTick(pos, this,level.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay));
    }


    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        flowInto(gasFlowGetter.flowGas(randomSource,pos,world),state,world,pos,randomSource);
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
