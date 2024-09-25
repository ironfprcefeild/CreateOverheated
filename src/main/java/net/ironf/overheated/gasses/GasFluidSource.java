package net.ironf.overheated.gasses;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

public class GasFluidSource extends ForgeFlowingFluid.Source {

    public GasFluidSource(Properties properties) {
        super(properties);
    }


    @Override
    public void tick(Level level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            RegistryObject<? extends GasBlock> gb = GasMapper.InvFluidGasMap.get(fluidState.getFluidType());
            level.setBlock(pos, gb.get().defaultBlockState(), 3);
            level.scheduleTick(pos,gb.get(),2);
        }
    }




}
