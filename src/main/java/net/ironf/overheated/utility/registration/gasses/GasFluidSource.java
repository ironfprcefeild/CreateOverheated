package net.ironf.overheated.utility.registration.gasses;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class GasFluidSource extends ForgeFlowingFluid {
    public BlockEntry<? extends GasBlock> createdBlock;
    public static BlockEntry<? extends GasBlock> setGasBlock;
    public GasFluidSource(Properties properties) {
        super(properties);
        createdBlock = setGasBlock;
    }

    @Override
    public boolean isSource(FluidState p_76140_) {
        return true;
    }

    @Override
    public int getAmount(FluidState p_164509_) {
        return 8;
    }


    public BlockState getCreatedBlockState(){
        return createdBlock.get().defaultBlockState();
    }
    @Override
    protected void spread(LevelAccessor levelAccessor, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            levelAccessor.setBlock(pos, getCreatedBlockState(), 3);
            levelAccessor.scheduleTick(pos,createdBlock.get(),createdBlock.get().upperTickDelay);
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            level.setBlock(pos, getCreatedBlockState(), 3);
            level.scheduleTick(pos,createdBlock.get(),createdBlock.get().upperTickDelay);
        }
    }
}
