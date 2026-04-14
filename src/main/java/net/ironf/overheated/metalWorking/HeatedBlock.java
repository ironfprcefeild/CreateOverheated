package net.ironf.overheated.metalWorking;

import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class HeatedBlock extends Block {

    public HeatedBlock(Properties p) {
        super(p);
    }

    public static final IntegerProperty TEMPERATURE = IntegerProperty.create("temperature",0,24);
    public static final IntegerProperty HEATLEVEL = IntegerProperty.create("heatlevel",1,3);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(TEMPERATURE).add(HEATLEVEL));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(TEMPERATURE,0).setValue(HEATLEVEL,1);
    }

    /*TODO add insulators,
      - Normal Bricks
      - Hot gas blocks
      - Flue gasses
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        //Schedule next tick
        level.scheduleTick(pos, this, 40, TickPriority.NORMAL);


        //Lower Heat Level if temp has dropped too low.
        if (state.getValue(TEMPERATURE) < (state.getValue(HEATLEVEL)-1)*8){
            state.setValue(HEATLEVEL,state.getValue(HEATLEVEL)-1);
        }

        //Find Blocks to Dissipate too
        int uninsulatedFaces = 0;
        ArrayList<BlockPos> dissipationTargets = new ArrayList<>();
        for (Direction d : Iterate.directions){
            BlockPos check = pos.relative(d);
            if (!AllTags.AllBlockTags.INSULATOR.matches(level.getBlockState(check))){
                uninsulatedFaces++;
                if (level.getBlockState(check).hasProperty(TEMPERATURE)){
                    dissipationTargets.add(check);
                }
            }
        }

        //Dissipate heating (only if we have enough heat to spread evenly
        if (!dissipationTargets.isEmpty() && dissipationTargets.size() <= state.getValue(TEMPERATURE)){
            for (BlockPos dissipate : dissipationTargets){
                TemperatureHandler.changeTempAt(level,dissipate,1,state.getValue(HEATLEVEL));
            }
        }

        //Loose heat
        state.setValue(TEMPERATURE,Math.max(0,state.getValue(TEMPERATURE) - uninsulatedFaces));
    }


}
