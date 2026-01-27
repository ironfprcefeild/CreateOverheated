package net.ironf.overheated.steamworks.blocks.reinforcement;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReinforcementBlock extends Block {
    public ReinforcementBlock(Properties p_49795_) {
        super(p_49795_);
    }

    /*
    @Override
    public boolean canSurvive(BlockState p_60525_, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.above()).is(AllBlocks.MECHANICAL_PRESS.get());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        if (level.getBlockEntity(pos.above()) instanceof MechanicalPressBlockEntity MPBE){
            MPBE.basinChecker.scheduleUpdate();
        }
    }

    @Override
    public void onRemove(BlockState p_60515_, Level level, BlockPos pos, BlockState p_60518_, boolean p_60519_) {
        if (level.getBlockEntity(pos.above()) instanceof MechanicalPressBlockEntity MPBE){
            MPBE.basinChecker.scheduleUpdate();
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()))
            return null;
        return defaultBlockState();
    }

     */
}
