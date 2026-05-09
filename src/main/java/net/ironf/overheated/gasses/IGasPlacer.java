package net.ironf.overheated.gasses;

import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;


public interface IGasPlacer {

    default void placeGasBlock( BlockPos pos, Block gb, Level level){
        level.setBlock(pos,gb.defaultBlockState(),4);
        //level.scheduleTick(pos,gb,4, TickPriority.NORMAL);
    }

    default void placeGasBlock(BlockPos pos, OverheatedRegistrate.FluidRegistration gas, Level level){
        placeGasBlock(pos,GasMapper.InvGasMap.get(gas).get(),level);
    }
    default void placeGasFluid(BlockPos pos, FluidStack gas, Level level){

        placeGasBlock(pos,GasMapper.InvFluidGasMap.get(gas.getFluid().getFluidType()).get(),level);
    }
}
