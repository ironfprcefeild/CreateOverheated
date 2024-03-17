package net.ironf.overheated.steamworks.steamFluids.inWorldSteam;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.infrastructure.worldgen.AllLayerPatterns;
import net.ironf.overheated.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SteamBlock extends Block {
    public SteamBlock(Properties p) {
        super(p);
    }
    public static final IntegerProperty pressure = IntegerProperty.create("pressure",1,4);
    public static final IntegerProperty heatRating = IntegerProperty.create("heat_rate",0,3);
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(pressure).add(heatRating));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        context.getLevel().scheduleTick(context.getClickedPos(),AllBlocks.STEAM.get(),6);
        return defaultBlockState().setValue(pressure,1).setValue(heatRating,1);
    }



    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        //TODO fix lag
        Direction randomShift =
                randomSource.nextIntBetweenInclusive(0,4) == 4 || world.getBlockState(pos).isAir()
                ? Iterate.horizontalDirections[randomSource.nextIntBetweenInclusive(0, 3)]
                : Direction.UP;

        BlockPos target = pos.relative(randomShift);
        BlockState targetState = world.getBlockState(target);
        if (world.isInWorldBounds(target)) {
            if (targetState.isAir()) {
                addSteam(world, target, pressure.getValue("pressure").orElse(1), heatRating.getValue("heat_rate").orElse(1));
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {
                world.scheduleTick(pos, AllBlocks.STEAM.get(), 4);
            }
        } else {
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

    }


    //Use theese method to add steam to the world
    public static void addSteam(ServerLevel world, BlockPos at, int pressurelevel, int heatrate){
        if (!world.isLoaded(at)){
            return;
        }
        world.setBlockAndUpdate(at,
                AllBlocks.STEAM.getDefaultState()
                        .setValue(pressure, pressurelevel)
                        .setValue(heatRating,heatrate));
        world.scheduleTick(at, AllBlocks.STEAM.get(),world.random.nextIntBetweenInclusive(1,5), TickPriority.LOW);
    }

    public static void addSteam(Level level, BlockPos at, int pressurelevel, int heatrate){
        if (!level.isLoaded(at)){
            return;
        }
        level.setBlockAndUpdate(at,
                AllBlocks.STEAM.getDefaultState()
                        .setValue(pressure, pressurelevel)
                        .setValue(heatRating,heatrate));
        level.scheduleTick(at, AllBlocks.STEAM.get(),level.random.nextIntBetweenInclusive(1,5), TickPriority.LOW);
    }


}
