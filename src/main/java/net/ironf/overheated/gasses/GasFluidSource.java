package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;


public class GasFluidSource extends BaseFlowingFluid.Source {

    public GasFluidSource(BaseFlowingFluid.Properties properties) {
        super(properties);
    }


    @Override
    public void tick(Level level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            DeferredHolder<Block,? extends GasBlock> gb = GasMapper.InvFluidGasMap.get(fluidState.getFluidType());
            level.setBlock(pos, gb.get().defaultBlockState(), 3);
            level.scheduleTick(pos,gb.get(),2);
        }
    }




}
