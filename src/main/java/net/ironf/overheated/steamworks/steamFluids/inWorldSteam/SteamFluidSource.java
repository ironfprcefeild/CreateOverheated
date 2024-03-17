package net.ironf.overheated.steamworks.steamFluids.inWorldSteam;

import com.mojang.datafixers.TypeRewriteRule;
import net.ironf.overheated.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class SteamFluidSource extends ForgeFlowingFluid {
    public int HeatRating;
    public int PressureLevel;
    public static int nextHeatRating = 0;
    public static int nextPressureLevel = 0;
    public SteamFluidSource(Properties properties)
    {
        super(properties);
        this.HeatRating = SteamFluidSource.nextHeatRating;
        this.PressureLevel = SteamFluidSource.nextPressureLevel;
    }
    public int getAmount(FluidState state) {
        return 8;
    }
    public boolean isSource(FluidState state) {
        return true;
    }

    @Override
    protected void spread(LevelAccessor levelAccessor, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            levelAccessor.setBlock(
                    pos,
                    AllBlocks.STEAM.getDefaultState().setValue(SteamBlock.pressure,PressureLevel).setValue(SteamBlock.heatRating,HeatRating),
                    3
            );
            levelAccessor.scheduleTick(pos,AllBlocks.STEAM.get(),7);
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()){
            SteamBlock.addSteam(level,pos,PressureLevel,HeatRating);
        }
    }
}
