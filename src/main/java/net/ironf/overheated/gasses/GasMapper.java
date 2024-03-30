package net.ironf.overheated.gasses;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.ironf.overheated.Overheated;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class GasMapper {
    //Maps Gas Blocks to Gas Fluids, added too when gasses are registered
    public static HashMap<RegistryObject<? extends GasBlock>, FluidEntry<ForgeFlowingFluid.Flowing>> GasMap = new HashMap<>();
    public static HashMap<FluidEntry<ForgeFlowingFluid.Flowing>,RegistryObject<? extends GasBlock>> InvGasMap = new HashMap<>();
    public static HashMap<FluidType,RegistryObject<? extends GasBlock>> InvFluidGasMap = new HashMap<>();


    public static void prepareGasBlockInfo(){
        Overheated.LOGGER.info("Preparing Gas Block Info");
        for (RegistryObject<? extends GasBlock> gb : GasMap.keySet()){
            InvFluidGasMap.put(GasMap.get(gb).get().getFluidType(),gb);
        }
    }



}
