package net.ironf.overheated.gasses;

import net.ironf.overheated.metalWorking.HeatedBlock;
import net.ironf.overheated.metalWorking.TemperatureHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class HotGasBlock extends GasBlock{
    public HotGasBlock(Properties p, GasFlowGetter gfg, Predicate<BlockState> flowThroughTest, int temperatureTransfer, int heatLevelTransfer, int lowerTickDelay, int upperTickDelay) {
        super(p, gfg, flowThroughTest, lowerTickDelay, upperTickDelay);
        this.temperatureTransfer = temperatureTransfer;
        this.heatLevelTransfer = heatLevelTransfer;
    }

    public int temperatureTransfer;
    public int heatLevelTransfer;

    @Override
    public void flowInto(BlockPos target, @NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        if (world.isInWorldBounds(target)) {
            BlockState targetState = world.getBlockState(target);
            if (flowThroughTest.test(targetState)) {
                //Non Heatable valid state
                world.setBlockAndUpdate(target, world.getBlockState(pos));
                world.setBlockAndUpdate(pos, targetState);
            } else if (targetState.hasProperty(HeatedBlock.TEMPERATURE)) {
                //Heatable state
                TemperatureHandler.changeTempAt(world,target,temperatureTransfer,heatLevelTransfer);
                world.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
            } else {
                world.scheduleTick(pos, this, world.random.nextIntBetweenInclusive(lowerTickDelay, upperTickDelay), TickPriority.NORMAL);
            }
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
