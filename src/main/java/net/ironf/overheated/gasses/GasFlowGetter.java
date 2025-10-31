package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public interface GasFlowGetter {
    BlockPos flowGas(RandomSource rs, BlockPos pos, ServerLevel world);
}
