package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public interface GasFlowGetter {
    BlockPos flowGas(RandomSource rs, BlockPos pos, ServerLevel world);
}
