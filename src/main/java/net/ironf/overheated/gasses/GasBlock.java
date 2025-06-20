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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GasBlock extends Block {
    public GasBlock(Properties p, int shiftChance, int pressurizeChance, int lowerTickDelay, int upperTickDelay, Direction direction) {
        super(p);
        this.shiftChance = shiftChance;
        this.pressurizeChance = pressurizeChance;
        this.upperTickDelay = upperTickDelay;
        this.lowerTickDelay = lowerTickDelay;
        this.direction = direction;
    }


    protected final int shiftChance;
    protected final int pressurizeChance;
    protected final int upperTickDelay;
    protected final int lowerTickDelay;

    protected final Direction direction;


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState other_state, boolean bool) {
        super.onPlace(state, level, pos, other_state, bool);
        level.scheduleTick(pos, this,level.random.nextIntBetweenInclusive(lowerTickDelay,upperTickDelay));
    }




    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {

        BlockPos target = pos.relative(
                (shiftChance == 0)
                        ? direction
                        : randomSource.nextIntBetweenInclusive(0,shiftChance) == shiftChance
                            ? Iterate.horizontalDirections[randomSource.nextIntBetweenInclusive(0, 3)]
                            : direction);
        if (world.isInWorldBounds(target)) {
            BlockState targetState = world.getBlockState(target);
            if (targetState == Blocks.AIR.defaultBlockState()) {
                world.setBlockAndUpdate(target, world.getBlockState(pos));
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {


                world.scheduleTick(pos, this, world.random.nextIntBetweenInclusive(lowerTickDelay, upperTickDelay), TickPriority.NORMAL);
            }
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }



    }


}
