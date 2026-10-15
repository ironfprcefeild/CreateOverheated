package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class HotGasBlock extends GasBlock{
    public HotGasBlock(Properties p, GasFlowGetter gfg, Predicate<BlockState> flowThroughTest, float thermalCoeff, float passiveTemp, int lowerTickDelay, int upperTickDelay) {
        super(p, gfg, flowThroughTest, lowerTickDelay, upperTickDelay);

    }



}
