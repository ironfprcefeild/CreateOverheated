package net.ironf.overheated.gasses;

import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GasBlock extends Block {
    public GasBlock(Properties p, int shiftChance, int lowerTickDelay, int upperTickDelay, Direction direction) {
        super(p);
        this.shiftChance = shiftChance;
        this.upperTickDelay = upperTickDelay;
        this.lowerTickDelay = lowerTickDelay;
        this.direction = direction;
        Overheated.LOGGER.info(String.valueOf(this.lowerTickDelay));
    }


    protected final int shiftChance;
    protected final int upperTickDelay;
    protected final int lowerTickDelay;

    protected final Direction direction;



    public static int setShiftChance;
    public static int setUpperTickDelay;
    public static int setLowerTickDelay;
    public static Direction setDirection;

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        context.getLevel().scheduleTick(context.getClickedPos(), this,1);
        return defaultBlockState();
    }

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, level, pos, p_60569_, p_60570_);
        level.scheduleTick(pos, this,1);
    }


    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        Direction randomShift =
                randomSource.nextIntBetweenInclusive(0,shiftChance) == shiftChance
                        ? Iterate.horizontalDirections[randomSource.nextIntBetweenInclusive(0, 3)]
                        : direction;
        BlockPos target = pos.relative(randomShift);
        if (world.isInWorldBounds(target)) {
            BlockState targetState = world.getBlockState(target);
            if (targetState == Blocks.AIR.defaultBlockState()) {
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
