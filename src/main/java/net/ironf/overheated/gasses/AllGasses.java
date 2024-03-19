package net.ironf.overheated.gasses;

import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllGasses {


    public static final FluidEntry<ForgeFlowingFluid.Flowing> testGas = REGISTRATE.gas("test_gas",GasFluidSource::new)
            .register(REGISTRATE.gasBlock("test_gas",GasBlock::new)
                    .heavierThanAir()
                    .shiftChance(2)
                    .tickDelays(1,1)
                    .register());

    public static void register(){
    }


}
