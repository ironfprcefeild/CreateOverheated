package net.ironf.overheated.gasses;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

public class GasFluidSource extends ForgeFlowingFluid.Flowing {

    public GasFluidSource(Properties properties) {
        super(properties);
    }


    @Override
    public boolean isSource(FluidState p_76140_) {
        return true;
    }

    @Override
    public int getAmount(FluidState p_164509_) {
        return 8;
    }

    @Override
    protected void spread(LevelAccessor levelAccessor, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            RegistryObject<? extends GasBlock> gb = GasMapper.InvFluidGasMap.get(fluidState.getFluidType());
            levelAccessor.setBlock(pos, gb.get().defaultBlockState(), 3);
            levelAccessor.scheduleTick(pos,gb.get(),2);
        }
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
