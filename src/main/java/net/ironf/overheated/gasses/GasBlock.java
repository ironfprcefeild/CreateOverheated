package net.ironf.overheated.gasses;

import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GasBlock extends Block {
    public GasBlock(Properties p) {
        super(p);
        heavierThanAir = setHeavierThanAir;
        shiftChance = setShiftChance;
        upperTickDelay = setUpperTickDelay;
        lowerTickDelay = setLowerTickDelay;
    }

    public Boolean heavierThanAir;
    public int shiftChance;
    public int upperTickDelay;
    public int lowerTickDelay;

    public static Boolean setHeavierThanAir = true;
    public static int setShiftChance = 4;
    public static int setUpperTickDelay = 6;
    public static int setLowerTickDelay = 1;


    public Boolean isHeavierThanAir() {
        return heavierThanAir;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        context.getLevel().scheduleTick(context.getClickedPos(), this,1);
        return defaultBlockState();
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        //TODO fix lag
        Direction randomShift =
                randomSource.nextIntBetweenInclusive(0,shiftChance) == shiftChance || world.getBlockState(pos).isAir()
                        ? Iterate.horizontalDirections[randomSource.nextIntBetweenInclusive(0, 3)]
                        : (heavierThanAir ? Direction.DOWN : Direction.UP);

        BlockPos target = pos.relative(randomShift);
        BlockState targetState = world.getBlockState(target);
        if (world.isInWorldBounds(target)) {
            if (targetState.isAir()) {
                add(world, target,world.getBlockState(pos));
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {
                world.scheduleTick(pos, this, world.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay));
            }
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

    }

    //Use theese method to add steam to the world
    public void add(ServerLevel world, BlockPos at, BlockState state){
        if (!world.isLoaded(at)){
            return;
        }
        world.setBlockAndUpdate(at, state);
        world.scheduleTick(at, this,world.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay), TickPriority.LOW);
    }

    public void add(Level level, BlockPos at, BlockState state){
        if (!level.isLoaded(at)){
            return;
        }
        level.setBlockAndUpdate(at, state);
        level.scheduleTick(at, this,level.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay), TickPriority.LOW);
    }
}
