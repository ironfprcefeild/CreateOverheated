package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ExplodingGasBlock extends GasBlock{
    public ExplodingGasBlock(Properties p, GasFlowGetter gfg, Predicate<BlockState> flowThroughTest, int explosionChance, int lowerTickDelay, int upperTickDelay) {
        super(p, gfg, flowThroughTest, lowerTickDelay, upperTickDelay);
        this.explosionChance = explosionChance;
    }

    public int explosionChance;

    //TODO hydrogen shouldn't explode while moving through anything but air
    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        BlockPos target = (gasFlowGetter.flowGas(randomSource,pos,world));

        if (world.isInWorldBounds(target)) {
            BlockState targetState = world.getBlockState(target);
            if (flowThroughTest.test(targetState)) {
                world.setBlockAndUpdate(target, world.getBlockState(pos));
                world.setBlockAndUpdate(pos, targetState);
            } else {
                world.scheduleTick(pos, this, world.random.nextIntBetweenInclusive(lowerTickDelay, upperTickDelay), TickPriority.NORMAL);
            }
            if (targetState.isAir() && world.random.nextIntBetweenInclusive(0, explosionChance) == explosionChance){
                //RAHH EXPLODE
                world.explode(null,pos.getX(),pos.getY(),pos.getZ(),2f, Level.ExplosionInteraction.TNT);
            }
        } else {
            world.explode(null,pos.getX(),pos.getY(),pos.getZ(),2f, Level.ExplosionInteraction.TNT);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
