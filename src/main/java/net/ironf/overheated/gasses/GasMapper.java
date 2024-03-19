package net.ironf.overheated.gasses;

import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.HashMap;

public class GasMapper {
    //Maps Gas Blocks to Gas Fluids, added too when gasses are registered
    public static HashMap<BlockEntry<? extends GasBlock>, FluidEntry<? extends ForgeFlowingFluid.Flowing>> GasMap = new HashMap<>();


}
