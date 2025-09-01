package net.ironf.overheated.gasses;

import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.fluids.FluidStack;

public interface IGasPlacer {

    default void placeGasBlock( BlockPos pos, Block gb, Level level){
        level.setBlock(pos,gb.defaultBlockState(),3);
        level.scheduleTick(pos,gb,2, TickPriority.HIGH);
    }

    default void placeGasBlock(BlockPos pos, OverheatedRegistrate.FluidRegistration gas, Level level){
        placeGasBlock(pos,GasMapper.InvGasMap.get(gas).get(),level);
    }

    default void placeGasBlock(BlockPos pos, FluidStack gas, Level level){

        placeGasBlock(pos,GasMapper.InvFluidGasMap.get(gas.getFluid().getFluidType()).get(),level);
    }
}
