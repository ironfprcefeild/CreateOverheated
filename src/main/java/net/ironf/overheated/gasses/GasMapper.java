package net.ironf.overheated.gasses;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.utility.registration.OverheatedRegistrate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class GasMapper {
    //Maps Gas Blocks to Gas Fluids, added too when gasses are registered
    public static HashMap<DeferredHolder<Block,? extends GasBlock>, OverheatedRegistrate.FluidRegistration> GasMap = new HashMap<>();
    public static HashMap<BlockState, OverheatedRegistrate.FluidRegistration> RawGasMap = new HashMap<>();

    public static HashMap<OverheatedRegistrate.FluidRegistration,DeferredHolder<Block,? extends GasBlock>> InvGasMap = new HashMap<>();
    public static HashMap<FluidType,DeferredHolder<Block,? extends GasBlock>> InvFluidGasMap = new HashMap<>();

    public static ArrayList<DeferredHolder<Block,? extends GasBlock>> nonCapturableGases = new ArrayList<>();

    public static void prepareGasBlockInfo(){
        Overheated.LOGGER.info("O: Preparing Gas Block Info");
        for (DeferredHolder<Block, ? extends GasBlock> gb : GasMap.keySet()){
            InvFluidGasMap.put(GasMap.get(gb).FLUID_TYPE.get(),gb);
        }
        for (DeferredHolder<Block, ? extends GasBlock> gb : GasMap.keySet()){
            RawGasMap.put(gb.get().defaultBlockState(),GasMap.get(gb));
        }
        for (DeferredHolder<Block, ? extends GasBlock> gb : nonCapturableGases){
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
