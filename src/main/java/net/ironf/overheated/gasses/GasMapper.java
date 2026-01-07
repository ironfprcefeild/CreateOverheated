package net.ironf.overheated.gasses;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GasMapper {
    //Maps Gas Blocks to Gas Fluids, added too when gasses are registered
    public static HashMap<RegistryObject<? extends GasBlock>, OverheatedRegistrate.FluidRegistration> GasMap = new HashMap<>();
    public static HashMap<BlockState, OverheatedRegistrate.FluidRegistration> RawGasMap = new HashMap<>();

    public static HashMap<OverheatedRegistrate.FluidRegistration,RegistryObject<? extends GasBlock>> InvGasMap = new HashMap<>();
    public static HashMap<FluidType,RegistryObject<? extends GasBlock>> InvFluidGasMap = new HashMap<>();

    public static ArrayList<FluidType> lightGasses = new ArrayList<>();

    public static ArrayList<RegistryObject<?extends GasBlock>> nonCapturableGases = new ArrayList<>();

    public static void prepareGasBlockInfo(){
        Overheated.LOGGER.info("O: Preparing Gas Block Info");
        for (RegistryObject<? extends GasBlock> gb : GasMap.keySet()){
            InvFluidGasMap.put(GasMap.get(gb).FLUID_TYPE.get(),gb);
        }
        for (RegistryObject<? extends GasBlock> gb : GasMap.keySet()){
            RawGasMap.put(gb.get().defaultBlockState(),GasMap.get(gb));
        }
        for (RegistryObject<?extends GasBlock> gb : nonCapturableGases){
            RawGasMap.remove(gb.get().defaultBlockState());
        }
    }

    public static boolean isGas(FluidStack fs){
        return InvFluidGasMap.containsKey(fs.getFluid().getFluidType());
    }


    public static boolean isHeavyGas(FluidStack fs){
        return InvFluidGasMap.containsKey(fs.getFluid().getFluidType());
    }


    public static boolean isLightGas(FluidStack fs){
        return InvFluidGasMap.containsKey(fs.getFluid().getFluidType());
    }



}
